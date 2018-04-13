package main.com.revoker;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class RevokerImpl implements Revoker{
	
	private static final String CACHE_NAME = "addressCache";
    private static final String CACHE_FIELD = "cache";
    private static final String EXPIRATION = "expiration";
    private static final String ADDRESS = "address";

    @Override
    public DNSCacheEntry getDNSCacheEntry(String hostname) {
        if (hostname == null) {
            System.out.println("hostname parameter is null so returning null!");
            return null;
        }

        try {
            final Map<String, Object> cache = getAddressCacheMap();

            for (Map.Entry<String, Object> entry : cache.entrySet()) {
                if (hostname.equals(entry.getKey())) {

                    final Object cacheEntry = entry.getValue();
                    final Class cacheEntryClass = cacheEntry.getClass();
                    final Field expirationField = cacheEntryClass.getDeclaredField(EXPIRATION);
                    expirationField.setAccessible(true);

                    final long expires = (Long) expirationField.get(cacheEntry);
                    final Field addressField = cacheEntryClass.getDeclaredField(ADDRESS);
                    addressField.setAccessible(true);
                    return new DNSCacheEntry(entry.getKey(),
                                             new Date(expires),
                                             Arrays.asList(
                                                 (InetAddress[]) addressField.get(cacheEntry)));
                }
            }
        } catch (NoSuchFieldException e) {
            System.out.println("NoSuchFieldException occurred which should not have happened");
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException occurred which should not have happened");
            System.out.println(e);
        }

        return null;
    }

    @Override
    public boolean revokeDNSCacheEntry(String hostname) {
        if (hostname == null) {
            System.out.println("hostname parameter is null so returning false!");
            return false;
        }

        try {
            final Map<String, Object> cache = getAddressCacheMap();
            final Iterator<Map.Entry<String, Object>> cacheIterator = cache.entrySet().iterator();
            boolean found = false;

            while (cacheIterator.hasNext()) {
                Map.Entry<String, Object> entry = cacheIterator.next();
                if (hostname.equals(entry.getKey())) {
                    System.out.println("Found the entry for the hostname so removing it");
                    cacheIterator.remove();
                    found = true;
                }
            }

            return found;
        } catch (NoSuchFieldException e) {
            System.out.println("NoSuchFieldException occurred which should not have happened");
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException occurred which should not have happened");
            System.out.println(e);
        }

        return false;
    }

    private Map<String, Object> getAddressCacheMap()
        throws NoSuchFieldException, IllegalAccessException {
        final Field cacheName = InetAddress.class.getDeclaredField(CACHE_NAME);
        cacheName.setAccessible(true);

        final Object addressCache = cacheName.get(null);
        final Class addressCacheClass = addressCache.getClass();
        final Field cacheField = addressCacheClass.getDeclaredField(CACHE_FIELD);
        cacheField.setAccessible(true);

        return (Map<String, Object>) cacheField.get(addressCache);
    }
}
