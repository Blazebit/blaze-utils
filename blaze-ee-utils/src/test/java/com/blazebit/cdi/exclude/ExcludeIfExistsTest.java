package com.blazebit.cdi.exclude;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


public class ExcludeIfExistsTest {

    @Test
    @Ignore
    public void testDeployment() throws Exception {
        CdiContainer container = CdiContainerLoader.getCdiContainer();
        container.boot();
        container.getContextControl().startContexts();

        TestBean testBean = BeanProvider.getContextualReference(TestBean.class);

        Assert.assertEquals("1", testBean.getExcludeInterface1().getName());
        Assert.assertEquals(Integer.valueOf(1), testBean.getInteger());
        Assert.assertEquals("Hello", testBean.getDisposableObject().getString());

        container.shutdown();
        Assert.assertTrue(DisposableProducer.disposeCalled);
    }
}
