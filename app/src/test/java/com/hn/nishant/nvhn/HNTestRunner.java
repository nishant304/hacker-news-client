package com.hn.nishant.nvhn;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

/**
 * Created by nishant on 22.03.17.
 */

public class HNTestRunner extends RobolectricTestRunner {

    public HNTestRunner(final Class<?> testClass) throws Exception{
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String myAppPath = RobolectricTestRunner.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath();
        String manifestPath =  "src/main/AndroidManifest.xml";
        String resPath = "src/main/res";
        String assetPath = myAppPath + "../../../src/main/assets";
        return new AndroidManifest(Fs.fileFromPath(manifestPath), Fs.fileFromPath(resPath),
                Fs.fileFromPath(assetPath)) {
            @Override
            public int getTargetSdkVersion() {
                return 25;
            }

            @Override
            public String getThemeRef(String activityClassName) {
                return "@style/RoboAppTheme";
            }

            @Override
            public String getPackageName() {
                return super.getPackageName();
            }
        };
    }
}
