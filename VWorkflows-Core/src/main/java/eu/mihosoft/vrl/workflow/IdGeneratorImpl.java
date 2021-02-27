/*
 * Copyright 2012-2021 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * Please cite the following publication(s):
 *
 * M. Hoffer, C.Poliwoda, G.Wittum. Visual Reflection Library -
 * A Framework for Declarative GUI Programming on the Java Platform.
 * Computing and Visualization in Science, 2011, in press.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */
package eu.mihosoft.vrl.workflow;

import java.util.HashSet;
import java.util.Set;

/**
 * This class generates ids for nodes and connectors
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
class IdGeneratorImpl implements IdGenerator {

    private Set<String> ids = new HashSet<>();
//    private int lastId = 0;

    public IdGeneratorImpl() {
        //
    }

    @Override
    public void addId(String id) {
        ids.add(id);
    }

    @Override
    public void addIds(IdGenerator generator) {
        ids.addAll(generator.getIds());
    }

    @Override
    public String newId(String prefix) {

        // TODO improve id generation
        // Question: do we really want strings as id?
        int counter = 0;//lastId + 1;
       
        
        if (prefix != null && !prefix.isEmpty() && !prefix.endsWith(":")) {
            prefix = prefix + "-";
        }

        String id = prefix + counter;

        while (ids.contains(id)) {
            id = prefix + counter;
            counter++;
        }

        ids.add(id);

//        lastId = counter;

        return id;
    }

    @Override
    public String newId() {
        return newId("");
    }

    @Override
    public Set<String> getIds() {
        Set<String> result = new HashSet<>(ids);
        return result;
    }

    @Override
    public IdGenerator newChild() {
        return this;
    }

}
