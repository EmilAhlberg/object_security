JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
		$(JC) $(JFLAGS) $*.java

CLASSES = \
	Server.java \
	SocketDispatcher.java \
	SocketListener.java \
	MessageMonitor.java 

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class
