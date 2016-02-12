package eu.dubedout.lazyloading;

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

    public <T> void add(T instance) {
        instanciatedObjectMap.put(instance.getClass(), instance);
    }

    public void addLazy(LazySetter lazySetter) {

    }


    public <T> T get(Class<T> clazz) {
        return (T) instanciatedObjectMap.get(clazz);
    }

    public <T> void get(Class<T> clazz, ResponseHandler<T> responseHandler) {
        T myInstance = (T) new Object();
        responseHandler.onInstanceReceived(myInstance);
    }

}
