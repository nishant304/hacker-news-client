package com.hn.nishant.nvhn.network;

import com.google.firebase.FirebaseApp;
import com.hn.nishant.nvhn.App;
import com.hn.nishant.nvhn.BuildConfig;
import com.hn.nishant.nvhn.HNTestRunner;
import com.hn.nishant.nvhn.model.Story;
import com.hn.nishant.nvhn.view.activity.StoryActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by nishant on 21.03.17.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class ,sdk =22)
@PrepareForTest({FirebaseApp.class})
public class RetrofitImplTest {


}
