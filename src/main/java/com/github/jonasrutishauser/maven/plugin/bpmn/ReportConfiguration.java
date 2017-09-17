package com.github.jonasrutishauser.maven.plugin.bpmn;

/*
 * Copyright (C) 2017 Jonas Rutishauser
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation version 3 of the License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.txt>.
 */

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

public class ReportConfiguration
{

    private final File basedir;

    private final List<File> bpmns;

    private final List<File> dmns;

    private final List<File> cmmns;

    public ReportConfiguration( File basedir, List<File> bpmns, List<File> dmns, List<File> cmmns )
    {
        this.basedir = basedir;
        this.bpmns = bpmns;
        this.dmns = dmns;
        this.cmmns = cmmns;
    }

    public boolean canGenerateReport()
    {
        return hasBpmn() || hasDmn() || hasCmmn();
    }

    public boolean hasBpmn()
    {
        return !bpmns.isEmpty();
    }

    public boolean hasDmn()
    {
        return !dmns.isEmpty();
    }

    public boolean hasCmmn()
    {
        return !cmmns.isEmpty();
    }

    public File getBasedir()
    {
        return basedir;
    }

    public List<File> getBpmns()
    {
        return bpmns;
    }

    public List<File> getDmns()
    {
        return dmns;
    }

    public List<File> getCmmns()
    {
        return cmmns;
    }

    public Stream<File> getModels()
    {
        return Stream.concat( bpmns.stream(), Stream.concat( dmns.stream(), cmmns.stream() ) );
    }

}
