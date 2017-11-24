package com.walnutin.eventbus;

import com.walnutin.entity.ServerUser;
import com.walnutin.entity.UserBean;

public class CommonUserResult {
    static public class CommonAddBindThirdResult {
        public int state;
        public String msg;

        public CommonAddBindThirdResult(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }

    static public class CommonUnBindThirdResult {
        public int state;
        public String msg;

        public CommonUnBindThirdResult(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }

    static public class CommonAddBindPhoneResult {
        public int state;
        public String msg;

        public CommonAddBindPhoneResult(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }

    static public class CommonChangeBindPhoneResult {
        public int state;
        public String msg;

        public CommonChangeBindPhoneResult(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }

    static public class CommomGetAccountAndPwdResult {
        public int state;
        public String msg;
        public ServerUser third_User;

        public CommomGetAccountAndPwdResult(int state, String msg) {
            this.state = state;
            this.msg = msg;
        }
    }


}