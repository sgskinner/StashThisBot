/*
 * GNU GENERAL PUBLIC LICENSE
 * Version 3, 29 June 2007
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * ArchiveThisBot - Summon this bot to archive Reddit URLs in archive.is
 * Copyright (C) 2016  S.G. Skinner
 */

package org.sgs.atbot.spring;

import java.util.HashMap;
import java.util.Map;

import org.sgs.atbot.service.AtbService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by S.G. Skinner on 2016-10-23
 */
public class SpringContext {
    private static final String SPRING_CONFIG_FILE = "spring-atbot.xml";
    private static final ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {SPRING_CONFIG_FILE});
    private static final Map<Class<? extends AtbService>, AtbService> classToServiceMap = new HashMap<>();


    public final static <T extends AtbService> T getService(Class<T> clazz) {

        if (clazz == null) {
            throw new RuntimeException("Class can't be null!");
        }

        T service;
        if (classToServiceMap.containsKey(clazz)) {
            service = (T) classToServiceMap.get(clazz);
        } else {
            service = context.getBean(clazz);
            if (service == null) {
                throw new RuntimeException("Couldn't find service for class: " + clazz.getName());
            }
            classToServiceMap.put(clazz, service);
        }

        return service;
    }

}
