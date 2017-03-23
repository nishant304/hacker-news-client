package com.hn.nishant.nvhn.network;

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
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

/**
 * Created by nishant on 21.03.17.
 */
@Config(constants = RetrofitImplTest.CustomBuildConfig.class)
@RunWith(HNTestRunner.class)
public class RetrofitImplTest {

    @Mock
    private StoryActivity storyActivity;

    @Mock
    private RetrofitImpl retrofit;

    @Captor
    private ArgumentCaptor<ResponseListener<Story>> callbackArgumentCaptor;

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        ActivityController<StoryActivity> controller = Robolectric.buildActivity(StoryActivity.class);
        storyActivity = controller.get();
       // controller.create();
    }

    @Test
    public void getAllIDs(){
        StoryActivity storyActivity = Robolectric.setupActivity(StoryActivity.class);
        Mockito.verify(retrofit).getStory(Mockito.anyLong(),callbackArgumentCaptor.capture());
        Story story = new Story();
        story.setRank(0);
        story.setBy(Mockito.anyString());
        story.setDescendants(0);
        story.setId(Mockito.anyInt());
        story.setTitle(Mockito.anyString());
        callbackArgumentCaptor.getValue().onSuccess(story);
        assert (storyActivity.getLayoutManager().getItemCount()!=0);
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
