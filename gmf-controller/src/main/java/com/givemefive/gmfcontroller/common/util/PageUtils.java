package com.givemefive.gmfcontroller.common.util;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import com.givemefive.gmfcontroller.common.base.page.Pageable;
import com.givemefive.gmfcontroller.common.base.page.Sort.Order;
import com.givemefive.gmfcontroller.common.exception.BusinessException;
import com.givemefive.gmfcontroller.common.exception.ErrorCode;

/**
 * 
 * @author guosk
 *
 */
public final class PageUtils {

  private PageUtils() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 填充分页信息
   * 
   * @param sql
   * @param page
   * @param obj Bean DO
   */
  public static void appendPage(StringBuilder sql, Pageable page, Class<?> beanClass) {
    appendSort(sql, page.getSort(), beanClass);
    sql.append(" limit ").append(page.getPageSize()).append(" offset ").append(page.getOffset());
  }

  /**
   * 填充分页信息
   * 
   * @param sql
   * @param page
   * @param obj List<String>
   */
  public static void appendPage(StringBuilder sql, Pageable page, List<String> validSortFileds) {
    appendSort(sql, page.getSort(), validSortFileds);
    sql.append(" limit ").append(page.getPageSize()).append(" offset ").append(page.getOffset());
  }

  public static void appendSort(StringBuilder sql, com.givemefive.gmfcontroller.common.base.page.Sort sort,
      Class<?> beanClass) {
    if (sort != null) {
      sql.append(" order by "); // order
      for (Iterator<Order> orderIt = sort.iterator(); orderIt.hasNext();) {
        Order order = orderIt.next();
        if (!validSortProperty(order.getProperty(), beanClass)) {
          throw new BusinessException(ErrorCode.COMMON_BASE_FORMAT_INVALID, "Invalid sort column");
        }
        sql.append(order.getProperty()).append(' ').append(order.getDirection());
        if (orderIt.hasNext()) {
          sql.append(',');
        }
      }
    }
  }

  /**
   * 填充排序信息
   * 
   * @param sql
   * @param page
   * @param obj List<String>
   */
  public static void appendSort(StringBuilder sql, com.givemefive.gmfcontroller.common.base.page.Sort sort,
      List<String> validSortFileds) {
    if (sort != null) {
      sql.append(" order by "); // order
      for (Iterator<Order> orderIt = sort.iterator(); orderIt.hasNext();) {
        Order order = orderIt.next();
        if (CollectionUtils.isNotEmpty(validSortFileds)
            && !validSortFileds.contains(order.getProperty())) {
          throw new BusinessException(ErrorCode.COMMON_BASE_FORMAT_INVALID, "Invalid sort column");
        }
        sql.append(order.getProperty()).append(' ').append(order.getDirection());
        if (orderIt.hasNext()) {
          sql.append(',');
        }
      }
    }
  }

  public static boolean validSortProperty(String property, Class<?> beanClass) {
    PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(beanClass,
        TextUtils.underLineToCamel(property));
    return descriptor != null;
  }
}
