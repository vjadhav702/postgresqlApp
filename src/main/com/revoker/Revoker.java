package main.com.revoker;

public interface Revoker {
	
	/**
     * Returns the {@link DNSCacheEntry} represented by the hostname in the {@link
     * java.net.InetAddress} cache.
     *
     * @param hostname - String representing the hostname whose cache entry is to be returned.
     * @return - {@link DNSCacheEntry} instance or null.
     */
    public DNSCacheEntry getDNSCacheEntry(String hostname);


    /**
     * Revokes the cache entry in the {@link java.net.InetAddress}.
     *
     * @param hostname - String representing the hostname whose cache entry is to be returned.
     * @return - true if there is an entry in the cache with the hostname and is removed otherwise
     *         false.
     */
    public boolean revokeDNSCacheEntry(String hostname);
}
