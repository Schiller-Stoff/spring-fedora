package org.sebi.springfedora.utils;

public class ValidationCommon {

  public static final String VALID_PID_REGEX = "^(o|context|query|corpus|cirilo):([a-z0-9])([-._a-z0-9]){1,}$";

  public static final String INVALID_PID_MESSAGE = "PID of digital object does not conform to required pattern!";
  
}
