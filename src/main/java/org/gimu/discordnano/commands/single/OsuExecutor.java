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

import org.gimu.discordnano.DiscordNano;
import org.gimu.discordnano.lib.NanoExecutor;
import org.gimu.discordnano.util.APIUtil;
import org.gimu.discordnano.lib.NanoMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class OsuExecutor extends NanoExecutor {

    public String[] triggers = {"osu"};
    public String description = "Fetches osu! profile";
    public String usage = "<user>";

    @Override
    public void respond(NanoMessage message, String[] args) throws IllegalArgumentException {
        String api = "https://osu.ppy.sh/api/get_user";
        StringBuilder sb = new StringBuilder();
        InputStream response = null;
        try {
            response = APIUtil.sendGet(api, "k=" + DiscordNano.config.getString("osu_api_key") + "&u=" + args[0]);

            BufferedReader streamReader = new BufferedReader(new InputStreamReader(response, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr = streamReader.readLine();
            while (inputStr != null) {
                responseStrBuilder.append(inputStr);
                inputStr = streamReader.readLine();
            }
            JSONObject jsonResponse = new JSONArray(responseStrBuilder.toString()).getJSONObject(0);
            sb.append("Username: " + jsonResponse.getString("username") + "\n");
            sb.append("Play_count: " + jsonResponse.getString("playcount") + "\n");
            sb.append("Accuracy: " + jsonResponse.getString("accuracy") + "\n");
            sb.append("Level: " + jsonResponse.getString("level") + "\n");
            sb.append("Country: " + jsonResponse.getString("country") + "\n");
            sb.append("Country_rank: " + jsonResponse.getString("pp_country_rank"));

        } catch (Exception e) {
            e.printStackTrace();
        }

        message.replyFramed("osu! Profile", sb.toString());
    }
}
