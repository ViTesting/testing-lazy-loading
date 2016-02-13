package eu.dubedout.lazyloading;

import android.os.Debug;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class LazyLoadingJava {
    private ConcurrentHashMap<Class, Object> instanciatedObjectMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Class, LazySetter> lazyObjectMap = new ConcurrentHashMap<>();

    public interface ResponseHandler<T> {
        void onInstanceReceived(T instance);
    }

    public interface LazySetter<T>{
        T get();
    }

    public <T> void addInstance(Class clazz, T instance) {
        if (instanciatedObjectMap.containsKey(clazz)) {
            Log.w(this.getClass().toString(), "Instance already added in the list, erasing the previous one");
        }

        instanciatedObjectMap.put(clazz, instance);
    }

    public <T> void addLazy(Class clazz, LazySetter<T> lazySetter) {
        if (lazyObjectMap.containsKey(clazz)) {
            Log.w(this.getClass().toString(), "LazySetter already added to the list, erasing the previous one");
            if (instanciatedObjectMap.containsKey(clazz)) {
                instanciatedObjectMap.remove(clazz);
            }
        }

        lazyObjectMap.put(clazz, lazySetter);
    }


    public <T> T get(Class<T> clazz) {
        if (instanciatedObjectMap.containsKey(clazz)) {
            return (T) instanciatedObjectMap.get(clazz);
        } else if (lazyObjectMap.containsKey(clazz)) {
            synchronized (clazz) {
                createClassInstance(clazz);
            }

            if (instanciatedObjectMap.containsKey(clazz)) {
                return (T) instanciatedObjectMap.get(clazz);
            }
        }

        throw new NullPointerException("Object have not been added to the LazyLoading class");
    }

    private <T> void createClassInstance(Class<T> clazz) {
        if (!instanciatedObjectMap.containsKey(clazz)) {
            Object instance = lazyObjectMap.get(clazz).get();
            if (instance != null) {
                instanciatedObjectMap.put(clazz, instance);
            }
        }
    }

    public <T> void get(Class<T> clazz, ResponseHandler<T> responseHandler) {
        T myInstance = (T) new Object();
        responseHandler.onInstanceReceived(myInstance);
    }

}
