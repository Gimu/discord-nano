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
package org.gimu.discordnano.commands.music;

import org.gimu.discordnano.util.HastebinUtil;
import org.gimu.discordnano.lib.NanoMessage;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

public class ListCommand {

    public static String respond(MusicLibrary library) {
        LinkedHashMap<String, JSONObject> musicLibraryMap = library.musicLibraryMap;

        if (musicLibraryMap.size() == 0) {
            return "The music library empty.";
        }
        StringBuilder response = new StringBuilder("__Music Library Status__ (Entries: " + musicLibraryMap.size() + ")\n\n");
        int iterator = 0;
        if (musicLibraryMap.size() <= 10) {
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                response.append("**" + iterator + "** " + entry.getKey() + " **<" + entry.getValue().getString("url") + ">\n");
                iterator++;
            }
        } else {
            StringBuilder body = new StringBuilder();
            for (Map.Entry<String, JSONObject> entry : musicLibraryMap.entrySet()) {
                body.append("**" + iterator + "** " + entry.getKey() + " **<" + entry.getValue().getString("url") + ">**\n");
                iterator++;
            }
            response.append(HastebinUtil.post(body.deleteCharAt(body.length()-1).toString()));
        }
        return response.toString();
    }
}
