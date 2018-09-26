JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	CacheMonitor.java \
	CachePoller.java \
	CacheDispatcher.java \
	CacheListener.java \
	MessageFactory.java \
	MessageMonitor.java \
	SocketDispatcher.java \
	SocketListener.java \
	Server.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
