package guang.crawler.controller;

import org.apache.zookeeper.Transaction;

public interface Zookeeperable
{
	public boolean delete(Transaction transaction) throws InterruptedException;
	
	public boolean load() throws InterruptedException;
	
	public boolean update(Transaction transaction) throws InterruptedException;
}
