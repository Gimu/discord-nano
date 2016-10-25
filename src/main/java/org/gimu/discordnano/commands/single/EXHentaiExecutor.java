/*
 *  Copyright 2016 Son Nguyen <mail@gimu.org>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.gimu.discordnano.commands.single;

import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.lib.NanoMessage;

public class EXHentaiExecutor extends NanoExecutor {

    private String[] triggers = {"exhentai"};
    private String description = "Display ex-hentai entry";
    private String usage = "";

    @Override
    public void respond(NanoMessage message, String[] args) {
        message.reply("D-don't push me! I'm working on it~");
    }
}
