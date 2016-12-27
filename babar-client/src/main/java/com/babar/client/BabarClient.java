package com.babar.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.babar.common.BabarRequest;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

@PropertySource("classpath:application.properties")
@Component
public class BabarClient {

	@Autowired
	private Environment env;

	@Autowired
	BabarClientChannelPoolHandler babarClientChannelPoolHandler;

	private ChannelPool pool;
	private static final Logger logger = LoggerFactory.getLogger(BabarClient.class);

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event){
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group);
		b.channel(NioSocketChannel.class);
		b.remoteAddress(env.getProperty("rpc.server.host"), Integer.parseInt(env.getProperty("rpc.server.port")));
		pool = new SimpleChannelPool(b, babarClientChannelPoolHandler);
		logger.info("Created simple channel pool on: " + env.getProperty("rpc.server.host") + ":" + env.getProperty("rpc.server.port"));
	}

	public void send(BabarRequest req){
		Future<Channel> f = pool.acquire();
		f.addListener(new FutureListener<Channel>() {
			@Override
			public void operationComplete(Future<Channel> future) throws Exception {
				if(f.isSuccess()){
					Channel ch = f.getNow();
					ch.writeAndFlush(req);
					logger.info("Send request to server with requestId: " + req.getRequestId());
					pool.release(ch);
				}
			}
		});
	}
}