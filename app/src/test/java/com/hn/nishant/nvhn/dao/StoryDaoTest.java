package com.hn.nishant.nvhn.dao;

import com.hn.nishant.nvhn.BuildConfig;
import com.hn.nishant.nvhn.model.Story;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;

import io.realm.Realm;

/**
 * Created by nishant on 22.03.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
@PrepareForTest({Realm.class})
public class StoryDaoTest {

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    Realm mockRealm;

    @Before
    public void setup() {
        PowerMockito.mockStatic(Realm.class);
        Realm mockRealm = PowerMockito.mock(Realm.class);
        PowerMockito.when(Realm.getDefaultInstance()).thenReturn(mockRealm);
        this.mockRealm = mockRealm;
    }

    @Test
    public void testRealm() throws IOException {
        Story story = new Story();
        PowerMockito.when(mockRealm.createObject(Story.class)).thenReturn(story);
        Story output = mockRealm.createObject(Story.class);
        Assert.assertThat(output, CoreMatchers.is(story));
    }

}
