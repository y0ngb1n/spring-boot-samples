package io.github.y0ngb1n.samples.datasource.druid.controller;

import com.alibaba.druid.stat.DruidStatManagerFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** @author yangbin */
@RestController
public class DruidStatController {

  @GetMapping("/druidStat")
  public Object druidStat() {
    return DruidStatManagerFacade.getInstance().getDataSourceStatDataList();
  }
}
