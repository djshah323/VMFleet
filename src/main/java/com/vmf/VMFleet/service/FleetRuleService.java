package com.vmf.VMFleet.service;

import com.vmf.VMFleet.dao.VehicleData;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jeasy.rules.api.Facts;
import org.jeasy.rules.api.Rules;
import org.jeasy.rules.api.RulesEngine;
import org.jeasy.rules.core.DefaultRulesEngine;
import org.jeasy.rules.mvel.MVELRuleFactory;
import org.jeasy.rules.support.reader.YamlRuleDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Service
public class FleetRuleService {

    private final RulesEngine rulesEngine = new DefaultRulesEngine();

    public enum MetricRules {
        Speed("speed_rule.yaml");

        @Getter
        private Rules rules;

        MetricRules(String fileName) {
            try {
                ClassPathResource resource = new ClassPathResource(fileName);
                InputStream inputStream = resource.getInputStream();
                MVELRuleFactory ruleFactory = new MVELRuleFactory(new YamlRuleDefinitionReader());
                rules = ruleFactory.createRules(new BufferedReader(new InputStreamReader(inputStream)));
            } catch (Exception ex) {
                log.warn(String.format("unable to load %s", fileName));
            }
        }
    }

    public void evaluateSpeedRules(VehicleData vehicleData) {
        Facts facts = new Facts();
        facts.put("vehicleData", vehicleData);
        rulesEngine.fire(MetricRules.Speed.getRules(), facts);
    }
}
