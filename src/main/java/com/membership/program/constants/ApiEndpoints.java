package com.membership.program.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiEndpoints {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Auth {
        public static final String BASE_URL="api/auth";

        public static final String LOG_IN ="/login";

        public static final String LOG_OUT = "/logout";
    }


    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class User{

        public static final String BASE_URL = "api/users";

        public static final String REGISTER_USER = "register";
        public static final String CURRENT_USER = "me";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Membership {
        public static final String BASE_URL = "api/v1/membership";

        public static final String PLANS = "/plans";
        public static final String PLANS_TIER = "/plans/tier/{tierLevel}";
        public static final String SUBSCRIBE = "/subscribe";
        public static final String SUBSCRIPTION_CURRENT = "/subscription/current";
        public static final String SUBSCRIPTION_HISTORY = "/subscription/history";
        public static final String SUBSCRIPTION_CANCEL = "/subscription/cancel";
        public static final String SUBSCRIPTION_RENEW = "/subscription/renew";
        public static final String STATUS = "/status";
        public static final String TIER_UPGRADE = "/tier/upgrade/{tierId}";
        public static final String TIER_DOWNGRADE = "/tier/downgrade/{tierId}";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class TierUpgrade {
        public static final String BASE_URL = "api/v1/tier-upgrade";

        public static final String EVALUATE = "/evaluate";
        public static final String EVALUATE_DETAILED = "/evaluate/detailed";
        public static final String ELIGIBILITY = "/eligibility";
        public static final String BEST_RULE = "/best-rule";
        public static final String APPLICABLE_RULES = "/applicable-rules";
        public static final String PROCESS_AUTO = "/process-auto";
        public static final String ADMIN_EVALUATE = "/admin/evaluate/{userId}";
        public static final String ADMIN_PROCESS_AUTO = "/admin/process-auto/{userId}";
    }
}
