package com.ibm.hybrid.cloud.sample.portfolio;

import javax.inject.Singleton;
import javax.servlet.annotation.WebFilter;

@Singleton
@WebFilter(filterName = "gzipFilter", urlPatterns = {"/*"})
public class GzipFilter {
}
