package e.nottdar.androidchat.ui.main_screen;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
import e.nottdar.androidchat.R;
import e.nottdar.androidchat.data.models.MessageModel;
import e.nottdar.androidchat.data.models.SignUpModel;
import e.nottdar.androidchat.data.pref.SignInPref;
import e.nottdar.androidchat.ui.adapter.MessageAdapter;
import e.nottdar.androidchat.ui.interfaces.AdapterCallBack;
import e.nottdar.androidchat.util.common_util.UtilExtra;

public class MainActivity extends AppCompatActivity implements AdapterCallBack {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final boolean LOG_DEBUG = true;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.etx)
    EditText etx;
    @BindView(R.id.btn)
    Button btn;

    private SignInPref mPref;
    private SignUpModel signUpModel;

    private MessageAdapter messageAdapter;
    private List<MessageModel> messageModelList = new ArrayList<>();
    private DatabaseReference mReference;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (LOG_DEBUG) Log.e(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpSharedPref();
        extractDataFromPref();

        setUpRecycler();
        setupConnection();
    }

    private void setUpSharedPref() {
        if (LOG_DEBUG) Log.v(TAG, " setUpSharedPref()");
        mPref = SignInPref.getmInstance(this);
    }

    private void extractDataFromPref() {
        if (LOG_DEBUG) Log.d(TAG, "extractDataFromPref()");
        signUpModel = new SignUpModel();
        signUpModel.setName(mPref.getUserName());
        signUpModel.setEmail(mPref.getUserEmail());
        signUpModel.setUID(mPref.getUSerUID());
        if (LOG_DEBUG)
            Log.i(TAG, signUpModel.toString());
    }

    private void setUpRecycler() {
        if (LOG_DEBUG) Log.e(TAG, "setUpRecycler()");
        messageAdapter = new MessageAdapter(messageModelList, this, this, signUpModel.getName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(messageAdapter);
    }


    private void setupConnection() {
        if (LOG_DEBUG) Log.e(TAG, "setupConnection()");
        mReference = FirebaseDatabase.getInstance().getReference();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (LOG_DEBUG) Log.e(TAG, "SUCCESS!");
                handleReturn(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                if (LOG_DEBUG) Log.e(TAG, "ERROR: " + databaseError.getMessage());
            }
        };
        //load first 50 messages
        mReference.child("messages").limitToFirst(150).addValueEventListener(valueEventListener);

    }

    private void handleReturn(DataSnapshot dataSnapshot) {
        if (LOG_DEBUG) Log.e(TAG, "handleReturn()");

        if (dataSnapshot != null) {

            messageModelList.clear();
            System.out.println("----children Count " + dataSnapshot.getChildrenCount());
            for (DataSnapshot item : dataSnapshot.getChildren()) {
                MessageModel data = item.getValue(MessageModel.class);
                messageModelList.add(data);
            }
            System.out.println(" modelList size" + messageModelList.size());
            messageAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
        }
    }

    @Override
    public void onLongClick(int position, View view) {
        System.out.println("-----------row pos : " + position);
        deleteDialog();
    }

    @OnClick(R.id.btn)
    public void onViewClicked() {
        System.out.println(" Tapped ...");
        String text = etx.getText().toString().trim();

        if (text.length() != 0 && !text.isEmpty()) {

            MessageModel messageModel = new MessageModel();
            messageModel.setUserName(signUpModel.getName());
            messageModel.setText(text);
            messageModel.setTime(System.currentTimeMillis());
            messageModel.setuID(signUpModel.getUID());
            messageModel.setImageName(signUpModel.getName());

            System.out.println("message model has " + messageModel);
            mReference.child("messages").push().setValue(messageModel);

            UtilExtra.hideKeyboard(this);
            etx.setText("");
        } else {
            etx.setError("duh ....write something..");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (LOG_DEBUG) Log.v(TAG, " onResume()");

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (LOG_DEBUG) Log.v(TAG, " onPause()");

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (LOG_DEBUG) Log.v(TAG, " onStop()");
        mReference.removeEventListener(valueEventListener);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (LOG_DEBUG) Log.v(TAG, " onDestroy()");
    }

    //fragTwo
    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle(" delete node msg ");
        builder.setMessage(" :( delete huh !");
        builder.setCancelable(true);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mReference != null) {
                    boolean ye = mReference.child("messages").removeValue().isSuccessful();
                    System.out.println(" ye " + ye);
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}
