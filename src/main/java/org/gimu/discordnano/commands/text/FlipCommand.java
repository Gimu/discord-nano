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
package org.gimu.discordnano.commands.text;

import org.gimu.discordnano.commands.AbstractCommand;
import org.gimu.discordnano.util.NanoMessage;

public class FlipCommand extends AbstractCommand {

    public String[] triggers = {"flip", "coin"};
    public String description = "Let Nano flip the coin";
    public String usage = "";

    @Override
    public void respond(NanoMessage message, String[] args) {
        double random = Math.random();
        if (random < 0.5) {
            message.reply("**Tails!**");
        } else {
            message.reply("**Heads!**");
        }
    }
}
