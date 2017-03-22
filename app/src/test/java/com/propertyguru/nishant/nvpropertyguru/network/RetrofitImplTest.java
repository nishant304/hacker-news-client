package com.propertyguru.nishant.nvpropertyguru.network;

import com.google.firebase.FirebaseApp;
import com.propertyguru.nishant.nvpropertyguru.App;
import com.propertyguru.nishant.nvpropertyguru.BuildConfig;
import com.propertyguru.nishant.nvpropertyguru.HNTestRunner;
import com.propertyguru.nishant.nvpropertyguru.model.Story;
import com.propertyguru.nishant.nvpropertyguru.view.activity.MainActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by nishant on 21.03.17.
 */
@Config(constants = RetrofitImplTest.CustomBuildConfig.class)
@RunWith(HNTestRunner.class)
public class RetrofitImplTest {

    @Mock
    private MainActivity mainActivity;

    @Mock
    private RetrofitImpl retrofit;

    @Captor
    private ArgumentCaptor<ResponseListener<Story>> callbackArgumentCaptor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ActivityController<MainActivity> controller = Robolectric.buildActivity(MainActivity.class);
        mainActivity = controller.get();
       // controller.create();
    }

    @Test
    public void getAllIDs(){
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        Mockito.verify(retrofit).getStory(Mockito.anyLong(),callbackArgumentCaptor.capture());
        Story story = new Story();
        story.setRank(0);
        story.setBy(Mockito.anyString());
        story.setDescendants(0);
        story.setId(Mockito.anyInt());
        story.setTitle(Mockito.anyString());
        callbackArgumentCaptor.getValue().onSuccess(story);
        assert (mainActivity.getLayoutManager().getItemCount()!=0);
    }

    public static final class CustomBuildConfig {
        public static final boolean DEBUG = BuildConfig.DEBUG;
        public static final String APPLICATION_ID = "com.correct.package.name";
        public static final String BUILD_TYPE = BuildConfig.BUILD_TYPE;
        public static final String FLAVOR = BuildConfig.FLAVOR;
        public static final int VERSION_CODE = BuildConfig.VERSION_CODE;
        public static final String VERSION_NAME = BuildConfig.VERSION_NAME;
    }

}
