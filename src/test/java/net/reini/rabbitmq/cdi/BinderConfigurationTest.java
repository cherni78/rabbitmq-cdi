package net.reini.rabbitmq.cdi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;

import java.net.URI;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.rabbitmq.client.Address;

import net.reini.rabbitmq.cdi.EventBinder.BinderConfiguration;

/**
 * Tests the binder configuration part.
 *
 * @author Patrick Reinhart
 */
@RunWith(MockitoJUnitRunner.class)
public class BinderConfigurationTest {
  @Mock
  private ConnectionConfigHolder config;

  private BinderConfiguration binderConfig;

  @Before
  public void setUp() {
    binderConfig = new BinderConfiguration(config);
  }

  /**
   * Test method for {@link BinderConfiguration#setHost(String)}.
   */
  @SuppressWarnings({"javadoc", "deprecation"})
  @Test
  public void testSetHost() {
    assertSame(binderConfig, binderConfig.setHost("hostName"));

    verify(config).addHost(new Address("hostName"));
  }

  /**
   * Test method for {@link BinderConfiguration#addHost(String)}.
   */
  @Test
  public void testAddHostString() {
    assertSame(binderConfig, binderConfig.addHost("hostName:123"));

    verify(config).addHost(new Address("hostName", 123));
  }

  /**
   * Test method for {@link BinderConfiguration#addHost(com.rabbitmq.client.Address)}.
   */
  @Test
  public void testAddHostAddress() {
    Address host = new Address("hostName", 1234);

    assertSame(binderConfig, binderConfig.addHost(host));

    verify(config).addHost(host);
  }

  /**
   * Test method for {@link BinderConfiguration#setUsername(String)}.
   */
  @Test
  public void testSetUsername() {
    assertSame(binderConfig, binderConfig.setUsername("username"));

    verify(config).setUsername("username");
  }

  /**
   * Test method for {@link BinderConfiguration#setPassword(String)}.
   */
  @Test
  public void testSetPassword() {
    assertSame(binderConfig, binderConfig.setPassword("password"));

    verify(config).setPassword("password");
  }

  /**
   * Test method for {@link BinderConfiguration#setVirtualHost(String)}.
   */
  @Test
  public void testSetVirtualHost() {
    assertSame(binderConfig, binderConfig.setVirtualHost("virtualHost"));

    verify(config).setVirtualHost("virtualHost");
  }

  /**
   * Test method for {@link BinderConfiguration#setSecure(boolean)}.
   */
  @Test
  public void testSetSecure() {
    assertSame(binderConfig, binderConfig.setSecure(true));
    assertSame(binderConfig, binderConfig.setSecure(false));

    verify(config).setSecure(true);
    verify(config).setSecure(false);
  }


  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_unkown_scheme() {
    try {
      binderConfig.setConnectionUri(URI.create("xXXx://flamingo.rmq.cloudamqp.com"));
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Wrong scheme in AMQP URI: xXXx", e.getMessage());
    }
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_unkown_host() {
    assertSame(binderConfig, binderConfig.setConnectionUri(URI.create("amqp://?")));

    verify(config).setHosts(Collections.singleton(new Address("127.0.0.1", 5672)));
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_unkown_credentials_part() {
    try {
      binderConfig.setConnectionUri(URI.create("amqp://xx:yyy:zzz@flamingo.rmq.cloudamqp.com"));
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Bad user info in AMQP URI: xx:yyy:zzz", e.getMessage());
    }
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_server_only() {
    assertSame(binderConfig,
        binderConfig.setConnectionUri(URI.create("Amqp://flamingo.rmq.cloudamqp.com/")));

    verify(config).setHosts(Collections.singleton(new Address("flamingo.rmq.cloudamqp.com", 5672)));
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_illegal_virtual_host() {
    try {
      binderConfig.setConnectionUri(URI.create("amqp://flamingo.rmq.cloudamqp.com/xxxx/vvv"));
      fail("IllegalArgumentException expected");
    } catch (IllegalArgumentException e) {
      assertEquals("Multiple segments in path of AMQP URI: /xxxx/vvv", e.getMessage());
    }
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_server_and_port() {
    assertSame(binderConfig,
        binderConfig.setConnectionUri(URI.create("amqp://flamingo.rmq.cloudamqp.com:1234")));

    verify(config).setHosts(Collections.singleton(new Address("flamingo.rmq.cloudamqp.com", 1234)));
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_secured_server_only() {
    assertSame(binderConfig,
        binderConfig.setConnectionUri(URI.create("amqps://flamingo.rmq.cloudamqp.com")));

    verify(config).setHosts(Collections.singleton(new Address("flamingo.rmq.cloudamqp.com", 5671)));
    verify(config).setSecure(true);
  }

  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_secured_server_user_port() {
    assertSame(binderConfig,
        binderConfig.setConnectionUri(URI.create("Amqps://test@flamingo.rmq.cloudamqp.com:1234")));

    verify(config).setUsername("test");
    verify(config).setHosts(Collections.singleton(new Address("flamingo.rmq.cloudamqp.com", 1234)));
    verify(config).setSecure(true);
  }


  /**
   * Test method for {@link BinderConfiguration#setConnectionUri(java.net.URI)}.
   */
  @Test
  public void testSetConnectionUri_secured() {
    assertSame(binderConfig, binderConfig
        .setConnectionUri(URI.create("amqps://user:password@flamingo.rmq.cloudamqp.com/nkjoriiy")));

    verify(config).setUsername("user");
    verify(config).setPassword("password");
    verify(config).setHosts(Collections.singleton(new Address("flamingo.rmq.cloudamqp.com", 5671)));
    verify(config).setSecure(true);
    verify(config).setVirtualHost("nkjoriiy");
  }
}