package com.patloew.rxwear;

import android.support.v4.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wearable.Wearable;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import rx.Observable;
import rx.Single;
import rx.observers.TestSubscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest({ Observable.class, ContextCompat.class, Wearable.class, Status.class, ConnectionResult.class, BaseRx.class })
@SuppressStaticInitializationFor("com.google.android.gms.wearable.Wearable")
public class RxWearTest extends BaseOnSubscribeTest {

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        super.setup();
    }

    // RxFit

    @Test
    public void setTimeout() {
        rxWear.setDefaultTimeout(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);
        assertEquals(TIMEOUT_TIME, (long) rxWear.timeoutTime);
        assertEquals(TIMEOUT_TIMEUNIT, rxWear.timeoutUnit);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setTimeout_TimeUnitMissing() {
        rxWear.setDefaultTimeout(TIMEOUT_TIME, null);
        assertNull(rxWear.timeoutTime);
        assertNull(rxWear.timeoutUnit);
    }

    @Test
    public void resetDefaultTimeout() {
        rxWear.setDefaultTimeout(TIMEOUT_TIME, TIMEOUT_TIMEUNIT);
        rxWear.resetDefaultTimeout();
        assertNull(rxWear.timeoutTime);
        assertNull(rxWear.timeoutUnit);
    }

    // GoogleApiClientObservable

    @Test
    public void GoogleAPIClientObservable_Success() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        GoogleAPIClientSingle single = PowerMockito.spy(new GoogleAPIClientSingle(ctx, new Api[] {}, new Scope[] {}));

        setupBaseSingleSuccess(single);
        Single.create(single).subscribe(sub);

        assertSingleValue(sub, apiClient);
    }

    @Test
    public void GoogleAPIClientObservable_ConnectionException() {
        TestSubscriber<GoogleApiClient> sub = new TestSubscriber<>();
        final GoogleAPIClientSingle single = PowerMockito.spy(new GoogleAPIClientSingle(ctx, new Api[] {}, new Scope[] {}));

        setupBaseSingleError(single);
        Single.create(single).subscribe(sub);

        assertError(sub, GoogleAPIConnectionException.class);
    }

}
