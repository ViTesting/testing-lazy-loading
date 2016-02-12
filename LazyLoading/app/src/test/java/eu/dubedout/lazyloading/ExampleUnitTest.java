//package eu.dubedout.lazyloading;
//
//import android.os.Handler;
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
///**
// * To work on unit tests, switch the Test Artifact in the Build Variants view.
// */
//public class ExampleUnitTest {
//    private LazyLoadingRegistry sut;
//
//    private ConcurrentHashMap<Class, LazyGetter> instantiationMap;
//    private ConcurrentHashMap<Class, Object> registryMap;
//    private Handler handler;
//
//    @Before
//    public void setUp() throws NoSuchFieldException, IllegalAccessException {
//        sut = new LazyLoadingRegistry();
//
//        instantiationMap = new ConcurrentHashMap<>();
//        InjectMockUtil.mockField(sut, "instantiationMap", instantiationMap);
//
//        registryMap = new ConcurrentHashMap<>();
//        InjectMockUtil.mockField(sut, "registryMap", registryMap);
//
//        handler = mock(Handler.class);
//        InjectMockUtil.mockField(sut, "uiHandler", handler);
//    }
//
//    @After
//    public void tearDown() {
//        LazyLoadingRegistry.clear();
//    }
//
//
//    @Test
//    public void testAddLazy() throws NoSuchFieldException, IllegalAccessException {
//        LazyGetter lazyGetter = new LazyGetter() {
//            @Override
//            public Object get() {
//                return new StringBuffer(12);
//            }
//        };
//
//        LazyLoadingRegistry.addLazy(StringBuffer.class, lazyGetter);
//
//        assertThat(instantiationMap.containsKey(StringBuffer.class)).isTrue();
//        assertThat(instantiationMap.get(StringBuffer.class)).isEqualTo(lazyGetter);
//    }
//
//    @Test
//    public void testAddLazy_doubleInit() throws NoSuchFieldException, IllegalAccessException {
//        LazyGetter lazyGetter = new LazyGetter() {
//            @Override
//            public Object get() {
//                return new StringBuffer(12);
//            }
//        };
//        LazyGetter lazyGetter2 = new LazyGetter() {
//            @Override
//            public Object get() {
//                return new StringBuffer(12);
//            }
//        };
//
//        LazyLoadingRegistry.addLazy(StringBuffer.class, lazyGetter);
//        LazyLoadingRegistry.addLazy(StringBuffer.class, lazyGetter2);
//
//        assertThat(instantiationMap.containsKey(StringBuffer.class)).isTrue();
//        assertThat(instantiationMap.get(StringBuffer.class)).isEqualTo(lazyGetter);
//    }
//
//    @Test
//    public void testAdd() throws Exception {
//        LazyLoadingRegistry.add(StringBuffer.class, new StringBuffer());
//
//        assertThat(registryMap.containsKey(StringBuffer.class)).isTrue();
//    }
//
//    @Test
//    public void testAdd_doubleInitialization() throws Exception {
//        StringBuffer instance = new StringBuffer();
//        LazyLoadingRegistry.add(StringBuffer.class, instance);
//        LazyLoadingRegistry.add(StringBuffer.class, new StringBuffer());
//
//        assertThat(registryMap.containsKey(StringBuffer.class)).isTrue();
//        assertThat(registryMap.get(StringBuffer.class)).isEqualTo(instance);
//    }
//
//
//    @Test
//    public void testGetInstance_whenNotInitialized() throws Exception {
//        NullPointerException exception = null;
//        try {
//            LazyLoadingRegistry.getInstance(StringBuffer.class);
//        } catch (NullPointerException e) {
//            exception = e;
//        }
//        assertThat(exception).isNotNull();
//    }
//
//    @Test
//    public void testGetInstance_whenObjectInjected() throws Exception {
//        StringBuffer instanceInjected = new StringBuffer();
//        LazyLoadingRegistry.add(StringBuffer.class, instanceInjected);
//
//        StringBuffer instance = LazyLoadingRegistry.getInstance(StringBuffer.class);
//
//        assertThat(instance).isEqualTo(instanceInjected);
//    }
//
//    @Test
//    public void testGetInstance_whenObjectInstantiationInjected() throws Exception {
//        Class aClass = StringBuffer.class;
//        LazyLoadingRegistry.addLazy(aClass, new LazyGetter() {
//            @Override
//            public Object get() {
//                return new StringBuffer();
//            }
//        });
//
//        StringBuffer instance = LazyLoadingRegistry.getInstance(StringBuffer.class);
//
//        assertThat(instance).isInstanceOf(aClass);
//    }
//
//    @Test
//    public void testGetInstance_whenObjectInstantiationInjected_isAlwaysSameInstance() throws Exception {
//        Class aClass = StringBuffer.class;
//        LazyLoadingRegistry.addLazy(aClass, new LazyGetter() {
//            @Override
//            public Object get() {
//                return new StringBuffer();
//            }
//        });
//
//        StringBuffer instance = LazyLoadingRegistry.getInstance(StringBuffer.class);
//        StringBuffer instance2 = LazyLoadingRegistry.getInstance(StringBuffer.class);
//
//        assertThat(instance).isInstanceOf(aClass);
//        assertThat(instance == instance2).isTrue();
//    }
//
//    @Test
//    public void testGetInstance_withThreads() throws InterruptedException {
//        int sleepTime = 150;
//        int launchedThreadNumber = 20;
//        CountDownLatch countDownLatch = new CountDownLatch(launchedThreadNumber);
//        final ArrayList<Object> list = new ArrayList<>();
//        registeringObjectWithTimer(sleepTime);
//        launchThreadsAndRegisterResultInList(launchedThreadNumber, list, countDownLatch);
//
//        boolean success = countDownLatch.await(2, TimeUnit.SECONDS);
//
//        Object instance = LazyLoadingRegistry.getInstance(Object.class);
//
//        assertThat(list.size()).isEqualTo(launchedThreadNumber);
//        assertThat(success).isTrue();
//        for (int position = 0; position < launchedThreadNumber; position++) {
//            // verify that we have always the same object returned even on a concurrent race
//            Assert.assertEquals("Position in list checked = " + position + "", list.get(position), instance);
//        }
//    }
//
//    private void launchThreadsAndRegisterResultInList(int threadLaunched, final ArrayList<Object> list, final CountDownLatch countDownLatch) {
//        for (int i = 0; i < threadLaunched; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    synchronized (list) {
//                        list.add(LazyLoadingRegistry.getInstance(Object.class));
//                        countDownLatch.countDown();
//                    }
//                }
//            }).start();
//        }
//    }
//
//    private void registeringObjectWithTimer(final int sleepTime) {
//        LazyLoadingRegistry.addLazy(Object.class, new LazyGetter() {
//            @Override
//            public Object get() {
//                try {
//                    Thread.sleep(sleepTime);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                return new Object();
//            }
//        });
//    }
//
//    @Ignore // TODO: VincentD 15-11-09 fix me
//    @Test
//    public void testStartInitializing() throws InterruptedException {
//        CountDownLatch countDownLatch = registerDataInitializatorInSut(100);
//
//        LazyLoadingRegistry.startInitializing();
//
//        boolean success = countDownLatch.await(2, TimeUnit.SECONDS);
//        assertThat(success).isTrue();
//        assertThat(registryMap.containsKey(Object.class)).isTrue();
//        assertThat(registryMap.containsKey(StringBuffer.class)).isTrue();
//        assertThat(registryMap.containsKey(String.class)).isTrue();
//    }
//
//    private CountDownLatch registerDataInitializatorInSut(final int totalInitializationTime) {
//        final int numberOfObjectsInitialized = 3;
//        final CountDownLatch countDownLatch = new CountDownLatch(3);
//        LazyLoadingRegistry.addLazy(Object.class, new LazyGetter() {
//            @Override
//            public Object get() {
//                try {
//                    Thread.sleep(totalInitializationTime / numberOfObjectsInitialized);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Object o = new Object();
//                countDownLatch.countDown();
//                return o;
//            }
//        });
//
//        LazyLoadingRegistry.addLazy(String.class, new LazyGetter() {
//            @Override
//            public Object get() {
//                try {
//                    Thread.sleep(totalInitializationTime / numberOfObjectsInitialized);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                String s = "toto";
//                countDownLatch.countDown();
//                return s;
//            }
//        });
//
//        LazyLoadingRegistry.addLazy(StringBuffer.class, new LazyGetter() {
//            @Override
//            public Object get() {
//                try {
//                    Thread.sleep(totalInitializationTime / numberOfObjectsInitialized);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                StringBuffer stringBuffer = new StringBuffer();
//                countDownLatch.countDown();
//                return stringBuffer;
//            }
//        });
//
//        return countDownLatch;
//    }
//
//    @Test
//    public void testGetInstanceWithCallback_ReturnSuccess() throws InterruptedException {
//        registerDataInitializatorInSut(100);
//
//        LazyLoadingRegistry.getInstance(Object.class, new ResponseHandler<Object>() {
//            @Override
//            public void onSuccess(Object responseObject) {
//                assertThat(responseObject).isNotNull();
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//                fail(error.getMessage(), error);
//            }
//        });
//        Thread.sleep(100);
//
//        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
//        verify(handler).post(runnableArgumentCaptor.capture());
//        Runnable value = runnableArgumentCaptor.getValue();
//        value.run();
//    }
//
//    @Test
//    public void testGetInstanceWithCallback_ReturnFailure() throws InterruptedException {
//        LazyLoadingRegistry.getInstance(Object.class, new ResponseHandler<Object>() {
//            @Override
//            public void onSuccess(Object responseObject) {
//                fail("No object instantiated, so it should not send a success response");
//            }
//
//            @Override
//            public void onFailure(Throwable error) {
//                assertThat(error).isNotNull();
//            }
//        });
//        Thread.sleep(100);
//
//        ArgumentCaptor<Runnable> runnableArgumentCaptor = ArgumentCaptor.forClass(Runnable.class);
//        verify(handler).post(runnableArgumentCaptor.capture());
//        Runnable value = runnableArgumentCaptor.getValue();
//        value.run();
//    }
//}