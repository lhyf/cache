package org.lhyf.cache.exception;

/****
 * @author YF
 * @date 2018-08-04 17:11
 * @desc CacheException
 *
 **/
public class CacheException extends RuntimeException {

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}
