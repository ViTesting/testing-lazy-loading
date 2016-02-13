package eu.dubedout.lazyloading;

import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class LazyLoadingJavaTest {

    private LazyLoadingJava sut;

    @Before
    public void setUp() {
        sut = new LazyLoadingJava();
        mockStatic(Log.class);
        when(Log.w(any(String.class), any(String.class))).thenReturn(0);
    }

    @Test
    public void testConstructor_whenCreating_thenReturnNotNull() {
        assertThat(new LazyLoadingJava()).isNotNull();
    }

    @Test
    public void testAdd_whenAddingString_thenReturnStringInstance() {
        String input = "test";
        sut.addInstance(String.class, input);
        String result = sut.get(input.getClass());

        assertThat(result).isEqualTo(input);
    }

    @Test
    public void testAdd_whenAddingTwoString_thenReturnLastOne(){
        String input1 = "test";
        sut.addInstance(String.class, input1);

        String input2 = "test2";
        sut.addInstance(String.class, input2);

        String result = sut.get(String.class);

        assertThat(result).isEqualTo(input2);
    }

    @Test
    public void testAddLazy_whenAddingString_thenReturnInstanciatedString() {
        final String input = new String("input1");

        sut.addLazy(String.class, new LazyLoadingJava.LazySetter() {
            @Override
            public Object get() {
                return input;
            }
        });

        String result = sut.get(String.class);

        assertThat(result).isEqualTo(input);
    }




}
