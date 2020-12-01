## Why JRE 8?
Apache Cassandra 3.x doesn't formally support JRE above 8, even if Zipkin has
some tricks to allow that. By using a normal JRE, we are able to keep the
example closer to real life.

*Note:* A DataStax Driver 4.x + Apache Cassandra 4.x example will be different.

## Why Jersey?
We need a simple frontend which doesn't add problems to an already fragile
classpath. Hence, this uses Jersey as the frontend with
`com.sun.net.httpserver.HttpServer`.

This is mostly to avoid conflicts with Guava and Netty, allowing a compatible
classpath for both the frontend and backend. Doing so eases the IDE experience:
You can debug the frontend and backend just like any other example.

The main thing we have to manage are things already managed in Apache Cassandra
3.x. Notably, it uses the DataStax driver internally, implying they've solved
Netty and Guava conflicts.

## Why Spring XML?
Jersey requires HK2 or CDI injection. We don't currently have CDI integration
with Brave, else we would use that.

There are some external ways to use integrate Spring with Jersey, but it is
confusing. Instead, we hide Spring inside `TracingConfiguration`. Internally,
this uses XML as it is declarative, but we could have easily used Java Config.
