package com.ytqf.signal.transport;

public class RetryLaterException extends Exception {
  public RetryLaterException() {}

  public RetryLaterException(Exception e) {
    super(e);
  }
}
