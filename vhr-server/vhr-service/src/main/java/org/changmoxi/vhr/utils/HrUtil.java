package org.changmoxi.vhr.utils;

import org.changmoxi.vhr.model.Hr;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author CZS
 * @create 2023-01-16 16:00
 **/
public class HrUtil {
    public static Hr getCurrentHr(){
        return ((Hr) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}