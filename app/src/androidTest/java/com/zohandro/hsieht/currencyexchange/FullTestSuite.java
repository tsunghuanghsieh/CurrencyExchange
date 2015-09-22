package com.zohandro.hsieht.currencyexchange;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by hsieht on 10/31/2014.
 */
public class FullTestSuite extends TestSuite {
    public FullTestSuite()
    {
        super();
    }

    public static Test suite()
    {
        return new TestSuiteBuilder(FullTestSuite.class).includeAllPackagesUnderHere().build();
    }
}
