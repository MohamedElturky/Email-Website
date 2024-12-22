package edu.alexu.mail.facade.proxy;

import edu.alexu.mail.facade.AttachmentServer;
import edu.alexu.mail.facade.AttachmentServerFacade;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Service
public class AttachmentServerProxy implements InvocationHandler {

    private final AttachmentServerFacade attachmentServerFacade;

    public AttachmentServerProxy(AttachmentServerFacade attachmentServerFacade) {
        this.attachmentServerFacade = attachmentServerFacade;
    }

    public AttachmentServer getProxyInstance() {
        return (AttachmentServer) Proxy.newProxyInstance(
                attachmentServerFacade.getClass().getClassLoader(),
                new Class[] {AttachmentServer.class},
                this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        attachmentServerFacade.connect();
        Object result = method.invoke(attachmentServerFacade, args);
        attachmentServerFacade.disconnect();
        return result;
    }
}
