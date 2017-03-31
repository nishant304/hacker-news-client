package com.hn.nishant.nvhn.util;

import com.hn.nishant.nvhn.model.Story;

import java.lang.ref.SoftReference;
import java.util.Stack;

/**
 * Created by nishant on 29.03.17.
 */

public class StoryObjPool {

    private Stack<SoftReference<Story>> stack = new Stack<>();

    public static StoryObjPool getInstance() {
        return storyObjPool;
    }

    private static StoryObjPool storyObjPool = new StoryObjPool();

    public Story getStory(){
        if(stack.isEmpty()){
            return null;
        }
        SoftReference<Story> storySoftReference = stack.pop();
        return storySoftReference.get();
    }

    public void putStory(Story story){
        stack.add(new SoftReference<Story>(story));
    }

}
