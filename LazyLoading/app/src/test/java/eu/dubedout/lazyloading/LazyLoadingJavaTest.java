package eu.dubedout.lazyloading;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class LazyLoadingJavaTest {
    private LazyLoadingJava sut;

    @Before
    public void setUp() {
        sut = new LazyLoadingJava();
    }

    @Test
    public void testConstructor_whenCreating_thenReturnNotNull() {
        assertThat(new LazyLoadingJava()).isNotNull();
    }

    @Test
    public void testAdd_whenAddingString_thenReturnStringInstance() {
        String input = "test";
        sut.add(input);
        String result = sut.get(input.getClass());

        assertThat(result).isEqualTo(input);
    }

    @Test
    public void testAdd_whenAddingTwoString_thenReturnLastOne(){

    }


}
