package redfox.chatroom.ui.main_screen;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bhargavms.dotloader.DotLoader;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import redfox.chatroom.R;
import redfox.chatroom.data.models.MessageModel;
import redfox.chatroom.data.models.SignUpModel;
import redfox.chatroom.data.pref.SignInPref;
import redfox.chatroom.network.NetMonitorCallbacks;
import redfox.chatroom.receiver.ConstantNetMonitorReceiver;
import redfox.chatroom.ui.adapter.MessageAdapter;
import redfox.chatroom.ui.interfaces.AdapterCallbacks;
import redfox.chatroom.ui.interfaces.LoadMessagesCallbacks;
import redfox.chatroom.ui.interfaces.NetDialogBtnCallbacks;
import redfox.chatroom.ui.login.LoginActivity;
import redfox.chatroom.ui.profile.Profile;
import redfox.chatroom.util.common_util.UtilConstant;
import redfox.chatroom.util.common_util.UtilExtra;
import redfox.chatroom.util.common_util.UtilImage;
import redfox.chatroom.util.common_util.UtilNetDialog;
import redfox.chatroom.util.common_util.UtilNetworkDetect;
import redfox.chatroom.util.common_util.UtilNotification;
import redfox.chatroom.util.firebase_util.UtilAuthentication;
import redfox.chatroom.util.firebase_util.UtilFBConstants;

import static redfox.chatroom.util.common_util.UtilConstant.NOT_TYPING;
import static redfox.chatroom.util.common_util.UtilConstant.TYPING;

public class MainActivity extends AppCompatActivity implements AdapterCallbacks, NetMonitorCallbacks, NetDialogBtnCallbacks, LoadMessagesCallbacks {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = false;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.etx)
    EditText etx;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.title_toolbar_tv)
    TextView titleToolbar;
    @BindView(R.id.name_user_toolbar)
    TextView userNameToolbar;
    @BindView(R.id.icon_toolbar)
    ImageView iconToolbar;
    @BindView(R.id.imv_refresh)
    ImageView imvRefresh;
    @BindView(R.id.text_dot_loader)
    DotLoader textDotLoader;
    @BindView(R.id.toolbar_net_imv)
    ImageView imvNet;
    @BindView(R.id.progBar)
    AnimatedCircleLoadingView mProgbar;

    private SignInPref mPref;
    private SignUpModel signUpModel;

    private MessageAdapter msgAdapter2;
    private List<MessageModel> messageModelList = new ArrayList<>();
    private DatabaseReference mRefChild; //directly referred to child "messages"
    private ValueEventListener velOneTime; //one time load at startup
    private ValueEventListener velOneTimeRefresh; //one time load when refreshed
    private ChildEventListener celLive; //live listener for specific child node changes - "messages"
    private Thread myWorker = null;
    private DatabaseReference mRefTyping;
    private ValueEventListener velLiveTyping; //live typing listener

    private boolean isNetThere;
    private ConstantNetMonitorReceiver mReceiver;
    private UtilNetDialog utilNetDialog;

    private boolean initialBlockPass;//checks conditional clause for showing dialog
    private LoadMessageThread mThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setUpTransition();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpProgress();
        setUpToolbar();
        setUpReceiver();
        setUpLoadMessageInThread();
        setUpSharedPref();
        extractDataFromPref();
        setUpRecycler();
        setUpNetDialog();

        if (UtilNetworkDetect.checkOnline(this)) {
            initialBlockPass = true;
            if (LOG_DEBUG) System.out.println("-------------------- net there : ");

            setUpDataBaseRef();
            readAllOneTimeListener();
            liveTypingListener();
            setUpEtxFocusListener();

        } else {
            initialBlockPass = false;
            if (LOG_DEBUG) System.out.println("xxxxxxxxxxxxxxxxxx  no net  : ");
        }
        loadProfileBackground();
        setUpPalette();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.am_signout:
                System.out.println("--");

                if (mPref != null) {
                    mPref.saveSignInStatus(0);
                } else {
                    SignInPref mPref = SignInPref.getmInstance(this);
                    mPref.saveSignInStatus(0);
                }
                UtilAuthentication.signOUT();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpTransition() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setEnterTransition(new Fade());
        }
    }

    private void setUpProgress() {
        mProgbar.startIndeterminate();
    }

    private void setUpToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpReceiver() {
        mReceiver = new ConstantNetMonitorReceiver(this);
        registerReceiver();
    }

    private void setUpLoadMessageInThread() {
        mThread = new LoadMessageThread(this, this);
    }

    private void setUpSharedPref() {
        if (LOG_DEBUG) Log.d(TAG, " setUpSharedPref()");
        mPref = SignInPref.getmInstance(this);
    }

    private void extractDataFromPref() {
        if (LOG_DEBUG) Log.d(TAG, "extractDataFromPref()");
        signUpModel = new SignUpModel();
        signUpModel.setName(mPref.getUserName());
        signUpModel.setEmail(mPref.getUserEmail());
        signUpModel.setUID(mPref.getUSerUID());
    }

    private void setUpRecycler() {
        if (LOG_DEBUG) Log.d(TAG, "setUpRecycler()");
        msgAdapter2 = new MessageAdapter(messageModelList, this, this, signUpModel.getName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(msgAdapter2);
    }

    private void setUpNetDialog() {
        utilNetDialog = new UtilNetDialog(this, MainActivity.this);
        utilNetDialog.initExitDialog();
    }

    private void setUpPalette() {
        String imgPath = mPref.getUserName().substring(0, 1).toLowerCase().concat(".png");

        Bitmap bitmap = UtilImage.getBitmapFromAssets(this, imgPath);
        Palette.from(bitmap).

                generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        //now here UI
                        if (palette != null) {
                            int extColor = palette.getVibrantColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                            userNameToolbar.setTextColor(extColor);
                        } else {
                            userNameToolbar.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.yellow));
                        }
                    }
                });
    }

    private void setUpDataBaseRef() {
        mRefChild = FirebaseDatabase.getInstance().getReference().child(UtilConstant.CHILD_1);
        mRefTyping = FirebaseDatabase.getInstance().getReference().child(UtilConstant.CHILD_2);
    }

    private void readAllOneTimeListener() {
        if (LOG_DEBUG) Log.d(TAG, "readAllOneTimeListener()-------------------------------------");
        if (mProgbar != null) mProgbar.setVisibility(View.VISIBLE);

        velOneTime = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (LOG_DEBUG) System.out.println("----------DELEGATE TO  THREAD-----------------");
                mThread.loadMessagesToListInBG(dataSnapshot, messageModelList, UtilConstant.ONE_TIME_OPERATION);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRefChild.addListenerForSingleValueEvent(velOneTime);

    }

    private void liveChildListener() {
        if (LOG_DEBUG) Log.d(TAG, "liveChildListener()--------------------------------------");

        if (messageModelList != null && messageModelList.size() > 0) {
            if (LOG_DEBUG) System.out.println("Kryptonium +++++++++++++++++++++++++++++++++++");

            celLive = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                    if (dataSnapshot != null) {
                        MessageModel messageModel = dataSnapshot.getValue(MessageModel.class);
                        if (messageModel != null) {

                            if (messageModel.getTime() == messageModelList.get(messageModelList.size() - 1).getTime()) {
                                //intentional

                            } else {
                                UtilNotification notification = new UtilNotification();
                                messageModelList.add(messageModel);
                                recyclerView.setAdapter(msgAdapter2);
                                msgAdapter2.notifyItemInserted(messageModelList.size() - 1);

                                if (mPref.getUserName().equalsIgnoreCase(messageModel.getUserName())) {
                                    //it's you, lol
                                } else {
                                    notification.createNotification(MainActivity.this,
                                            messageModel.getUserName(), messageModel.getText(), messageModel.getTime());
                                    UtilExtra.doVibrate(MainActivity.this);
                                    UtilExtra.playSound(MainActivity.this);
                                }

                            }
                        }
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };

            mRefChild.limitToLast(1).addChildEventListener(celLive);
        } else {
            if (LOG_DEBUG) System.out.println("BIRDMAN 000000000000000000000000000000000000000000");
        }
    }

    private void liveTypingListener() {
        if (LOG_DEBUG) System.out.println("liveTypingListener()");

        //No need to worry as we are dealing with only one entry
        velLiveTyping = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String VALUE = (String) dataSnapshot.getValue();
                if (LOG_DEBUG) System.out.println("Typing Status : " + VALUE);

                //do operation based on..typing
                if (VALUE != null) {
                    if (VALUE.equalsIgnoreCase(TYPING)) {
                        textDotLoader.setVisibility(View.VISIBLE);
                    } else if (VALUE.equalsIgnoreCase(NOT_TYPING)) {
                        textDotLoader.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mRefTyping.addValueEventListener(velLiveTyping);
    }

    private void setUpEtxFocusListener() {
        etx.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    if ((LOG_DEBUG)) Log.d(TAG, "etx : GOT Focus");
                    mRefTyping.setValue(TYPING);
                } else {
                    if ((LOG_DEBUG)) Log.d(TAG, "etx : LOST Focus");
                    mRefTyping.setValue(NOT_TYPING);
                }
            }
        });

    }

    @OnClick(R.id.btn)
    public void sendMessageBtn() {
        if (LOG_DEBUG) Log.d(TAG, "Send btn Tapped...");
        sendMessage();
        UtilExtra.hideKeyboard2(MainActivity.this, etx);

    }

    private void sendMessage() {
        String userInput = etx.getText().toString().trim();

        if (userInput.length() != 0 && !TextUtils.isEmpty(userInput)) {

            String text = UtilExtra.profanityFilter(userInput);

            MessageModel messageModel = new MessageModel();
            messageModel.setUserName(signUpModel.getName());
            messageModel.setText(text);
            messageModel.setTime(System.currentTimeMillis());
            messageModel.setuID(signUpModel.getUID());
            messageModel.setImageName(signUpModel.getName());

            mRefChild.push().setValue(messageModel);
            etx.setText("");
        } else {
            etx.setError("duh ....write something..");
        }
    }

    @OnClick(R.id.name_user_toolbar)
    public void onToolbarNameClicked() {
        if (mPref.getUserName().equalsIgnoreCase(UtilConstant.DEFAULT_NAME)) {
            //ask him to set a Name
            setUserName();
            //use onNewIntent -- to see the updates or restart app
        } else {
            Toast.makeText(this, " It's YOU, " + mPref.getUserName() + " :) LOL ", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (LOG_DEBUG) Log.d(TAG, "onNewIntent()");

        // when you setting username from this activity , after setting, you come here
        //so eith use onNewIntent to update the user name or restart the app
        if (mPref.getUserName().equalsIgnoreCase(UtilConstant.DEFAULT_NAME)) {

        } else {
            userNameToolbar.setText(mPref.getUserName());
        }
    }

    @OnClick(R.id.imv_refresh)
    public void onRefreshImv() {
        if (LOG_DEBUG) Log.d(TAG, "Refresh btn tapped...");
        imvRefresh.animate().setDuration(3000).rotationBy(360f).start();
        Toast.makeText(this, "Retrieving Latest Conversations..", Toast.LENGTH_SHORT).show();
        refreshListener();

    }

    private void refreshListener() {
        if (LOG_DEBUG) Log.d(TAG, "refreshListener()");
        if (mProgbar != null) mProgbar.setVisibility(View.VISIBLE);
        velOneTimeRefresh = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageModelList.clear();
                mThread.loadMessagesToListInBG(dataSnapshot, messageModelList, UtilConstant.REFRESH_OPERATION);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mRefChild.addListenerForSingleValueEvent(velOneTimeRefresh);
    }

    //this will trigger etx  setOnFocusChangeListener -  onFocus change() - NO FOCUS clause
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    UtilExtra.hideKeyboard(this);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    public void registerReceiver() {
        if (LOG_DEBUG) Log.d(TAG, " registering..");

        registerReceiver(mReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    public void unRegisterReceiver() {
        if (LOG_DEBUG) Log.d(TAG, "unregistered.......");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    @Override
    public void isContinuousNetCheck(boolean netStatus) {
        isNetThere = netStatus;
        if (LOG_DEBUG)
            System.out.println("------------------------------------ net status ---------------- " + netStatus);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isNetThere) {
                    imvNet.setImageResource(R.drawable.ic_net_available);
                } else {
                    imvNet.setImageResource(R.drawable.ic_net_unavailable);
                    showExitDialog();
                }
            }
        });
    }

    @Override
    public void onLongClick(int position, View view) {
        //Not implemented
    }

    private void showExitDialog() {
        utilNetDialog.showExitDialog();
    }

    @Override
    public void onPositiveBtnTapped() {

        if (initialBlockPass) {
            System.out.println(" NET WAS THERE BUT NOW NOT, SO RESTART");

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAndRemoveTask();
            else {
                finish();
            }
            startActivity(intent);
            //exit and restart
        } else {
            //exit
            System.out.println("NO NET DIRECTLY EXIT");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                finishAndRemoveTask();
            else {
                finish();
            }

        }

    }

    private void setUserName() {
        if (LOG_DEBUG) Log.d(TAG, "setUserName()");
        Bundle bundle = new Bundle();
        SignUpModel signUpModel = new SignUpModel();
        signUpModel.setName(mPref.getUserName()); // nada here
        signUpModel.setEmail(mPref.getUserEmail());
        signUpModel.setUID(mPref.getUSerUID());
        bundle.putParcelable(UtilFBConstants.PARCEL_KEY, signUpModel);
        startActivity(new Intent(this, Profile.class).putExtras(bundle));
    }

    @Override
    public void loadMessagesInBG(String message, String whichOperation) {
        if (LOG_DEBUG)
            System.out.println("---------------loadMessagesInBG()-----------------------------");

        if (whichOperation.equalsIgnoreCase(UtilConstant.ONE_TIME_OPERATION)) {
            if (LOG_DEBUG) System.out.println(" ONE TIME ");

            if (message.equalsIgnoreCase("FILLED")) {
                if (LOG_DEBUG) System.out.println(" FILLED + ONE TIME");

                if (LOG_DEBUG)
                    System.out.println("modelList size one Time Load: " + messageModelList.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgAdapter2.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(msgAdapter2.getItemCount());

                        liveChildListener();
                        if (mProgbar != null) mProgbar.setVisibility(View.GONE);


                    }
                });

            } else {
                if (LOG_DEBUG) System.out.println(" NOT FILLED + ONE TIME");
            }
        }
        if (whichOperation.equalsIgnoreCase(UtilConstant.REFRESH_OPERATION)) {
            if (LOG_DEBUG) System.out.println(" REFRESH ");

            if (message.equalsIgnoreCase("FILLED")) {
                if (LOG_DEBUG) System.out.println("  FILLED + REFRESH");

                if (LOG_DEBUG) Log.d(TAG, "modelList size : " + messageModelList.size());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        msgAdapter2.notifyDataSetChanged();
                        recyclerView.smoothScrollToPosition(msgAdapter2.getItemCount());

                        if (mProgbar != null) mProgbar.setVisibility(View.GONE);

                    }
                });

            } else {
                if (LOG_DEBUG) System.out.println(" NOT FILLED + REFRESH");
            }
        }
    }

    private void loadProfileBackground() {
        myWorker = new Thread(new Runnable() {
            @Override
            public void run() {
                String imgName = mPref.getUserName().substring(0, 1).toLowerCase().concat(".png");
                final Bitmap bitmap = UtilImage.getBitmapFromAssets(MainActivity.this, imgName);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        userNameToolbar.setText(mPref.getUserName());

                        if (bitmap != null) {
                            iconToolbar.setImageBitmap(bitmap);
                        } else {
                            iconToolbar.setImageResource(R.drawable.ic_pawprint);
                        }

                    }
                });
            }
        });
        myWorker.start();
        myWorker.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.d(TAG, " onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.d(TAG, " onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.d(TAG, " onStop()");

        if (mRefTyping != null) mRefTyping.setValue(NOT_TYPING);

        if (velOneTime != null) mRefChild.removeEventListener(velOneTime);

        if (velOneTimeRefresh != null) {
            mRefChild.removeEventListener(velOneTimeRefresh);
        }
        if (celLive != null) mRefChild.removeEventListener(celLive);

        if (velLiveTyping != null) mRefTyping.removeEventListener(velLiveTyping);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.d(TAG, " onDestroy()");

        unRegisterReceiver();
        mThread.destroyLoadMessageThread();
        if (myWorker != null) {
            Thread thread = myWorker;
            thread.interrupt();
            myWorker = null;
        }
        if (mReceiver != null) mReceiver.quitHandlerThread();

    }

}