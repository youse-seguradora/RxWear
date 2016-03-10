package com.patloew.rxwearsample;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.patloew.rxwear.RxWear;
import com.patloew.rxwear.transformers.DataEventGetSerializable;
import com.patloew.rxwear.transformers.DataItemGetSerializable;
import com.patloew.rxwear.transformers.MessageEventGetDataMap;

import rx.subscriptions.CompositeSubscription;

public class MainActivity extends WearableActivity {

    private BoxInsetLayout mContainerView;
    private TextView mTitleText;
    private TextView mMessageText;
    private TextView mPersistentText;

    private CompositeSubscription subscription = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTitleText = (TextView) findViewById(R.id.title);
        mMessageText = (TextView) findViewById(R.id.message);
        mPersistentText = (TextView) findViewById(R.id.persistent);

        RxWear.init(this);

        subscription.add(RxWear.Message.listen()
                .compose(MessageEventGetDataMap.filterByPath("/message"))
                .subscribe(dataMap -> {
                    mTitleText.setText(dataMap.getString("title", getString(R.string.no_message)));
                    mMessageText.setText(dataMap.getString("message", getString(R.string.no_message_info)));
                }, throwable -> Toast.makeText(this, "Error on message listen", Toast.LENGTH_LONG)));

        subscription.add(RxWear.Data.listen()
                .compose(DataEventGetSerializable.<String>filterByPathAndType("/persistentText", DataEvent.TYPE_CHANGED))
                .subscribe(text -> mPersistentText.setText(text),
                        throwable -> Toast.makeText(this, "Error on data listen", Toast.LENGTH_LONG)));

        subscription.add(RxWear.Data.get()
                .compose(DataItemGetSerializable.<String>filterByPath("/persistentText"))
                .subscribe(text -> mPersistentText.setText(text),
                        throwable -> Toast.makeText(this, "Error on getting DataItem", Toast.LENGTH_LONG)));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTitleText.setTextColor(getResources().getColor(android.R.color.white));
            mMessageText.setTextColor(getResources().getColor(android.R.color.white));
            mPersistentText.setTextColor(getResources().getColor(android.R.color.white));

        } else {
            mContainerView.setBackground(null);
            mTitleText.setTextColor(getResources().getColor(android.R.color.black));
            mMessageText.setTextColor(getResources().getColor(android.R.color.black));
            mPersistentText.setTextColor(getResources().getColor(android.R.color.black));
        }
    }
}
