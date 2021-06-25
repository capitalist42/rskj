/*
 * This file is part of RskJ
 * Copyright (C) 2019 RSK Labs Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package co.rsk.db;

import co.rsk.crypto.Keccak256;
import co.rsk.trie.Trie;
import org.ethereum.config.blockchain.upgrades.ActivationConfig;
import org.ethereum.config.blockchain.upgrades.ConsensusRule;
import org.ethereum.core.BlockHeader;
import org.ethereum.datasource.KeyValueDataSource;

import java.util.Objects;

public class StateRootHandler {
    private final ActivationConfig activationConfig;
    private final KeyValueDataSource stateRootDB;

    public StateRootHandler(
            ActivationConfig activationConfig,
            KeyValueDataSource stateRootDB) {
        this.activationConfig = activationConfig;
        this.stateRootDB = stateRootDB;
    }

    public Keccak256 translate(BlockHeader block) {
        boolean isRskip126Enabled = activationConfig.isActive(ConsensusRule.RSKIP126, block.getNumber());
        byte[] blockStateRoot = block.getStateRoot();
        if (isRskip126Enabled) {
            return new Keccak256(blockStateRoot);
        }

        byte[] stateRootHash = Objects.requireNonNull(
                stateRootDB.get(blockStateRoot),
                "Reset database or continue syncing with previous version"
        );

        return new Keccak256(stateRootHash);
    }

    public void register(BlockHeader executedBlock, Trie executionResult) {
        boolean isRskip126Enabled = activationConfig.isActive(ConsensusRule.RSKIP126, executedBlock.getNumber());
        if (isRskip126Enabled) {
            return;
        }

        stateRootDB.put(executedBlock.getStateRoot(), executionResult.getHash().getBytes());
    }
}
