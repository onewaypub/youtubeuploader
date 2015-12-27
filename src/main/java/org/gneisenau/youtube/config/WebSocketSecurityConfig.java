/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.gneisenau.youtube.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

/**
 * Allows configuring WebSocket Authorization.
 *
 * <p>
 * For example:
 * </p>
 *
 * <pre>
 * &#064;Configuration
 * public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
 *
 * 	&#064;Override
 * 	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
 * 		messages.simpDestMatchers(&quot;/user/queue/errors&quot;).permitAll().simpDestMatchers(&quot;/admin/**&quot;).hasRole(&quot;ADMIN&quot;)
 * 				.anyMessage().authenticated();
 * 	}
 * }
 * </pre>
 *
 *
 * @since 4.0
 * @author Rob Winch
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketSecurityConfig extends AbstractWebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/chat").withSockJS();
	}
	
}
//@Order(Ordered.HIGHEST_PRECEDENCE + 100)
//public class WebSocketSecurityConfig extends AbstractWebSocketMessageBrokerConfigurer
//		implements SmartInitializingSingleton {
//	private final WebSocketMessageSecurityMetadataSourceRegistry inboundRegistry = new WebSocketMessageSecurityMetadataSourceRegistry();
//
//	private SecurityExpressionHandler<Message<Object>> expressionHandler;
//
//	private ApplicationContext context;
//
//	@Override
//	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
//		argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
//	}
//
//	@Override
//	public final void configureClientInboundChannel(ChannelRegistration registration) {
//		ChannelSecurityInterceptor inboundChannelSecurity = inboundChannelSecurity();
//		registration.setInterceptors(securityContextChannelInterceptor());
//		if (!sameOriginDisabled()) {
//			registration.setInterceptors(csrfChannelInterceptor());
//		}
//		if (inboundRegistry!= null && inboundRegistry.containsMapping()) {
//			registration.setInterceptors(inboundChannelSecurity);
//		}
//		customizeClientInboundChannel(registration);
//	}
//
//	private PathMatcher getDefaultPathMatcher() {
//		try {
//			return context.getBean(SimpAnnotationMethodMessageHandler.class).getPathMatcher();
//		} catch (NoSuchBeanDefinitionException e) {
//			return new AntPathMatcher();
//		}
//	}
//
//	/**
//	 * Allows subclasses to customize the configuration of the
//	 * {@link ChannelRegistration} .
//	 *
//	 * @param registration
//	 *            the {@link ChannelRegistration} to customize
//	 */
//	protected void customizeClientInboundChannel(ChannelRegistration registration) {
//	}
//
//	@Bean
//	public CsrfChannelInterceptor csrfChannelInterceptor() {
//		return new CsrfChannelInterceptor();
//	}
//
//	@Bean
//	public ChannelSecurityInterceptor inboundChannelSecurity() {
//		ChannelSecurityInterceptor channelSecurityInterceptor = new ChannelSecurityInterceptor(
//				inboundMessageSecurityMetadataSource());
//		MessageExpressionVoter<Object> voter = new MessageExpressionVoter<Object>();
//		if (expressionHandler != null) {
//			voter.setExpressionHandler(expressionHandler);
//		}
//
//		List<AccessDecisionVoter<? extends Object>> voters = new ArrayList<AccessDecisionVoter<? extends Object>>();
//		voters.add(voter);
//
//		AffirmativeBased manager = new AffirmativeBased(voters);
//		channelSecurityInterceptor.setAccessDecisionManager(manager);
//		return channelSecurityInterceptor;
//	}
//
//	@Bean
//	public SecurityContextChannelInterceptor securityContextChannelInterceptor() {
//		return new SecurityContextChannelInterceptor();
//	}
//
//	@Bean
//	public MessageSecurityMetadataSource inboundMessageSecurityMetadataSource() {
//		if (expressionHandler != null) {
//			inboundRegistry.expressionHandler(expressionHandler);
//		}
//		configureInbound(inboundRegistry);
//		return inboundRegistry.createMetadataSource();
//	}
//
//
//	private static class WebSocketMessageSecurityMetadataSourceRegistry extends MessageSecurityMetadataSourceRegistry {
//		@Override
//		public MessageSecurityMetadataSource createMetadataSource() {
//			return super.createMetadataSource();
//		}
//
//		@Override
//		protected boolean containsMapping() {
//			return super.containsMapping();
//		}
//
//		@Override
//		protected boolean isSimpDestPathMatcherConfigured() {
//			return super.isSimpDestPathMatcherConfigured();
//		}
//	}
//
//	@Autowired
//	public void setApplicationContext(ApplicationContext context) {
//		this.context = context;
//	}
//
//	@Autowired(required = false)
//	public void setMessageExpessionHandler(List<SecurityExpressionHandler<Message<Object>>> expressionHandlers) {
//		if (expressionHandlers.size() == 1) {
//			this.expressionHandler = expressionHandlers.get(0);
//		}
//	}
//
//	public void afterSingletonsInstantiated() {
//		if (sameOriginDisabled()) {
//			return;
//		}
//
//		String beanName = "stompWebSocketHandlerMapping";
//		SimpleUrlHandlerMapping mapping = context.getBean(beanName, SimpleUrlHandlerMapping.class);
//		Map<String, Object> mappings = mapping.getHandlerMap();
//		for (Object object : mappings.values()) {
//			if (object instanceof SockJsHttpRequestHandler) {
//				SockJsHttpRequestHandler sockjsHandler = (SockJsHttpRequestHandler) object;
//				SockJsService sockJsService = sockjsHandler.getSockJsService();
//				if (!(sockJsService instanceof TransportHandlingSockJsService)) {
//					throw new IllegalStateException(
//							"sockJsService must be instance of TransportHandlingSockJsService got " + sockJsService);
//				}
//
//				TransportHandlingSockJsService transportHandlingSockJsService = (TransportHandlingSockJsService) sockJsService;
//				List<HandshakeInterceptor> handshakeInterceptors = transportHandlingSockJsService
//						.getHandshakeInterceptors();
//				List<HandshakeInterceptor> interceptorsToSet = new ArrayList<HandshakeInterceptor>(
//						handshakeInterceptors.size() + 1);
//				interceptorsToSet.add(new CsrfTokenHandshakeInterceptor());
//				interceptorsToSet.addAll(handshakeInterceptors);
//
//				transportHandlingSockJsService.setHandshakeInterceptors(interceptorsToSet);
//			} else if (object instanceof WebSocketHttpRequestHandler) {
//				WebSocketHttpRequestHandler handler = (WebSocketHttpRequestHandler) object;
//				List<HandshakeInterceptor> handshakeInterceptors = handler.getHandshakeInterceptors();
//				List<HandshakeInterceptor> interceptorsToSet = new ArrayList<HandshakeInterceptor>(
//						handshakeInterceptors.size() + 1);
//				interceptorsToSet.add(new CsrfTokenHandshakeInterceptor());
//				interceptorsToSet.addAll(handshakeInterceptors);
//
//				handler.setHandshakeInterceptors(interceptorsToSet);
//			} else {
//				throw new IllegalStateException("Bean " + beanName
//						+ " is expected to contain mappings to either a SockJsHttpRequestHandler or a WebSocketHttpRequestHandler but got "
//						+ object);
//			}
//		}
//
//		if (inboundRegistry.containsMapping() && !inboundRegistry.isSimpDestPathMatcherConfigured()) {
//			PathMatcher pathMatcher = getDefaultPathMatcher();
//			inboundRegistry.simpDestPathMatcher(pathMatcher);
//		}
//	}
//
//	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//		messages.nullDestMatcher().authenticated().simpSubscribeDestMatchers("/chat").permitAll()
//				.simpDestMatchers("/app/**").hasRole("USER").simpSubscribeDestMatchers("/topic/message").hasRole("USER")
//				.anyMessage().denyAll();
//
//	}
//
//	protected boolean sameOriginDisabled() {
//		return true;
//	}
//
//	public void configureMessageBroker(MessageBrokerRegistry config) {
//		config.enableSimpleBroker("/topic");
//		config.setApplicationDestinationPrefixes("/app");
//	}
//
//	@Override
//	public void registerStompEndpoints(StompEndpointRegistry registry) {
//		registry.addEndpoint("/chat").withSockJS();
//	}
//
//}