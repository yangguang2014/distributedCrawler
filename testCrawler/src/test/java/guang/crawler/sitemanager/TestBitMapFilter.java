package guang.crawler.sitemanager;

import guang.crawler.urlFilter.BitMapFilter;

import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class TestBitMapFilter
{
	private BitMapFilter	filter;
	
	@Before
	public void setup() throws NoSuchAlgorithmException
	{
		this.filter = new BitMapFilter();
	}
	
	@Test
	public void test()
	{
		String s = "http://static.blog.csdn.net/skin/default/css/style.css?v=1.1";
		Assert.assertFalse(this.filter.contains(s));
		Assert.assertFalse(this.filter.containsAndSet(s));
		Assert.assertTrue(this.filter.contains(s));
	}
	
}
