package eu.dubedout.lazyloading;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class LazyLoadingJava {
    private ConcurrentHashMap<Class, Object> instancesMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class, LazySetter> lazyInitializerMap = new ConcurrentHashMap<>();

    public interface Callback<T> {
        void onInstanceReceived(T instance);
    }

    public interface LazySetter<T>{
        T get();
    }

    public <T> void addInstance(Class clazz, T instance) {
        if (instancesMap.containsKey(clazz)) {
            Log.w(this.getClass().toString(), "Instance already added in the list, erasing the previous one");
        }

        instancesMap.put(clazz, instance);
    }

    public <T> void addLazy(Class clazz, LazySetter<T> lazySetter) {
        if (lazyInitializerMap.containsKey(clazz)) {
            Log.w(this.getClass().toString(), "LazySetter already added to the list, erasing the previous one");
            if (instancesMap.containsKey(clazz)) {
                instancesMap.remove(clazz);
            }
        }

        lazyInitializerMap.put(clazz, lazySetter);
    }


    public <T> T get(Class<T> clazz) {
        if (instancesMap.containsKey(clazz)) {
            return (T) instancesMap.get(clazz);
        } else if (lazyInitializerMap.containsKey(clazz)) {
            synchronized (clazz) {
                createClassInstance(clazz);
            }

            if (instancesMap.containsKey(clazz)) {
                return (T) instancesMap.get(clazz);
            }
        }

        throw new NullPointerException("Object have not been added to the LazyLoading class");
    }

    private <T> void createClassInstance(Class<T> clazz) {
        if (!instancesMap.containsKey(clazz)) {
            Object instance = lazyInitializerMap.get(clazz).get();
            if (instance != null) {
                instancesMap.put(clazz, instance);
            }
        }
    }

    public <T> void get(final Class<T> clazz, final Callback<T> callback) {
        final Handler uiThreadHandler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                final T instance = get(clazz);
                uiThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onInstanceReceived(instance);
                    }
                });
            }
        }).start();
    }

}
