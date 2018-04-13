package main.com.revoker;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

public class DNSCacheEntry {
	
	private final String hostname;
    private final Date expires;
    private final List<InetAddress> addresses;

    public DNSCacheEntry(String hostname, Date expires, List<InetAddress> addresses) {
        this.hostname = hostname;
        this.expires = expires;
        this.addresses = addresses;
    }

    public String getHostname() {
        return hostname;
    }

    public Date getExpires() {
        return expires;
    }

    public List<InetAddress> getAddresses() {
        return addresses;
    }
}
