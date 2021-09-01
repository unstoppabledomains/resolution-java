package com.unstoppabledomains.resolution;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

import com.unstoppabledomains.exceptions.ns.NSExceptionCode;
import com.unstoppabledomains.exceptions.ns.NamingServiceException;
import com.unstoppabledomains.resolution.naming.service.uns.L2Resolver;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

@SuppressWarnings("unchecked") // For mocking generic types
@ExtendWith(MockitoExtension.class)
public class L2ResolverTest {
  private L2Resolver resolver = new L2Resolver();
  
  protected <T> void mockExecutor(ExecutorService executor) {
    doAnswer(new Answer<T>() {
        public T answer(InvocationOnMock invocation) throws Exception {
            return ((Callable<T>) invocation.getArguments()[0]).call();
        }
    }).when(executor).submit(any(Callable.class));
  }
  
  @Test
  public void callsBothMethods() throws Exception {
    Callable<Object> mockCallable = mock(Callable.class);
    Callable<Object> mockCallable2 = mock(Callable.class);

    resolver.resolveOnBothLayers(mockCallable, mockCallable2);

    verify(mockCallable).call();
    verify(mockCallable2).call();
  }

  @Test
  public void returnsResultFromL2() throws Exception {
    Callable<String> mockCallable = mock(Callable.class);
    Callable<String> mockCallable2 = mock(Callable.class);

    when(mockCallable2.call()).thenReturn("test return value 2");
    when(mockCallable.call()).thenReturn("test return value 1");

    String result = resolver.resolveOnBothLayers(mockCallable, mockCallable2);

    verify(mockCallable).call();
    verify(mockCallable2).call();
    assertEquals("test return value 2", result);
  }

  @TestFactory
  public Collection<DynamicTest> throwsNSErrorsFromL2() throws Exception {
    Exception[] exceptions = {
      new NamingServiceException(NSExceptionCode.BlockchainIsDown),
      new NamingServiceException(NSExceptionCode.InvalidDomain),
      new NamingServiceException(NSExceptionCode.IncorrectMethodName),
      new NullPointerException(),
      new ArithmeticException(),
    };
    
    Collection<DynamicTest> dynamicTests = new ArrayList<>();

    for (Exception ex : exceptions) {
      Executable exec = () -> {
        Callable<String> mockCallable = mock(Callable.class);
        Callable<String> mockCallable2 = mock(Callable.class);
    
        when(mockCallable.call()).thenReturn("test return value 1");
        when(mockCallable2.call()).thenThrow(ex);
    
        Exception thrown = assertThrows(NamingServiceException.class, () -> resolver.resolveOnBothLayers(mockCallable, mockCallable2));
    
        verify(mockCallable).call();
        verify(mockCallable2).call();
        
        if (((NamingServiceException)thrown).getCode() != NSExceptionCode.UnknownError) {
          assertEquals(ex, thrown);
        } else {
          assertEquals("Unknown Error occurred", thrown.getMessage());
          assertEquals(ex, thrown.getCause());
        } 
      };

      String testName = "Throws errors from L2: " + ex.getMessage();
      
      dynamicTests.add(DynamicTest.dynamicTest(testName, exec));
    }

    return dynamicTests;
  }

  @Test
  public void returnsResultFromL1() throws Exception {
    Callable<String> mockCallable = mock(Callable.class);
    Callable<String> mockCallable2 = mock(Callable.class);

    when(mockCallable2.call()).thenThrow(new NamingServiceException(NSExceptionCode.UnregisteredDomain));
    when(mockCallable.call()).thenReturn("test return value 1");

    String result = resolver.resolveOnBothLayers(mockCallable, mockCallable2);

    verify(mockCallable).call();
    verify(mockCallable2).call();
    assertEquals("test return value 1", result);
  }

  @TestFactory
  public Collection<DynamicTest> throwsNSErrorsFromL1() throws Exception {
    Exception[] exceptions = {
      new NamingServiceException(NSExceptionCode.UnregisteredDomain),
      new NamingServiceException(NSExceptionCode.InvalidDomain),
      new NamingServiceException(NSExceptionCode.IncorrectMethodName),
      new NullPointerException(),
      new ArithmeticException(),
    };
    
    Collection<DynamicTest> dynamicTests = new ArrayList<>();

    for (Exception ex : exceptions) {
      Executable exec = () -> {
        Callable<String> mockCallable = mock(Callable.class);
        Callable<String> mockCallable2 = mock(Callable.class);
    
        when(mockCallable.call()).thenThrow(ex);
        when(mockCallable2.call()).thenThrow(new NamingServiceException(NSExceptionCode.UnregisteredDomain));
    
        Exception thrown = assertThrows(NamingServiceException.class, () -> resolver.resolveOnBothLayers(mockCallable, mockCallable2));
    
        verify(mockCallable).call();
        verify(mockCallable2).call();
        
        if (((NamingServiceException)thrown).getCode() != NSExceptionCode.UnknownError) {
          assertEquals(ex, thrown);
        } else {
          assertEquals("Unknown Error occurred", thrown.getMessage());
          assertEquals(ex, thrown.getCause());
        } 
      };

      String testName = "Throws errors from L1: " + ex.getMessage();
      
      dynamicTests.add(DynamicTest.dynamicTest(testName, exec));
    }

    return dynamicTests;
  }
}
