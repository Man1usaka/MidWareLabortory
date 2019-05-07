/*
 * JBoss, Home of Professional Open Source
 * Copyright 2015, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package yao.study.client;

import yao.study.remote.model.Admin;
import yao.study.remote.model.Alumni;
import yao.study.remote.stateful.RemoteEntryClerk;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.util.Hashtable;

/**
 * @author Y Jiang
 */
public class Client {

    public static void main(String[] args) throws Exception {
        final RemoteEntryClerk f = lookupRemoteEntryClerk();
        Admin admin = new Admin("root","123456");
        //登录
        System.out.println(f.login(admin));

        //删除已经超时
        Thread.sleep(5000);
        System.out.println("Delete: "+f.deleteAlumni(7L));

        //重新登录
        System.out.println(f.login(admin));

        //5秒后退出
        Thread.sleep(5000);
        System.out.println(f.logout(admin));

        //重新登录
        System.out.println(f.login(admin));

        System.out.println("Delete: "+f.deleteAlumni(7L));

        //插入100个校友
        for(Alumni alumni :f.generatorAlumni(100)){
            f.insertAlumni(alumni);
            System.out.println(alumni);
        };

        //更新校友名字
        Alumni testUpdateAlumni = new Alumni();
        testUpdateAlumni.setId(8L);
        testUpdateAlumni.setName("testUpdate");
        System.out.println("Updata: " +f.updateAlumni(testUpdateAlumni));

        //获得所有
        for(Alumni alumni:f.listAlumnu()){
            System.out.println(alumni);
        }

    }


    private static RemoteEntryClerk lookupRemoteEntryClerk() throws NamingException {
        final Hashtable<String, String> jndiProperties = new Hashtable<>();
        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(jndiProperties);
        return (RemoteEntryClerk) context.lookup("ejb:/wildfly-ejb-remote-server-side/EntryClerkBean!"
                + RemoteEntryClerk.class.getName() + "?stateful");
    }


}
