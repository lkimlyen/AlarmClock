/*
 * Copyright (c) 2016 咖枯 <kaku201313@163.com | 3772304@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.kaku.weac.Listener;

/**
 * Http访问返回回调接口
 *
 * @author 咖枯
 * @version 1.0 2015/8/29
 */
public interface HttpCallbackListener {

    /**
     * 加载完成
     *
     * @param response http返回信息
     */
    void onFinish(String response);

    /**
     * 加载失败
     *
     * @param e 错误信息
     */
    void onError(Exception e);
}
