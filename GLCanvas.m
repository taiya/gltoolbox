% Tested on Nov 5 2016
% - Matlab for OSX R2016b (JoGL version 2.3 compiled in OpenGL2 mode by 1.7.0_60-b19
% - java compiler: JDK7 Update 79
% 
% Note: not using the right version of the JDK to compile might cause the
% class file not to be seen by matlab (i.e. although the class file is in 
% the classpath, you cannot create it.
classdef GLCanvas
    properties
        hfigure = [];   %< figure handle
        jGLCanvas = [];   %< java class
        container = []; %< java component
    end
    
    methods
        %--- Constructor
        function this = GLCanvas()
            %--- Compile on demand
            if ~exist('JGLCanvas','class'), GLCanvas.compile(); end
            
            %--- Instantiate Java/Matlab resources
            this.hfigure = figure();
            this.jGLCanvas = JGLCanvas();
            %--- place the given Java component in the current figure
            [~, this.container] = javacomponent(this.jGLCanvas,[],this.hfigure);
            %--- make the java object occupy the full figure
            set(this.container,'Units', 'normalized');
            set(this.container,'Position', [0 0 1 1]); 
            %--- delete the old menu
            set(this.hfigure,'Toolbar','none')
            set(this.hfigure,'Menubar','none');
        end
        
        function draw_cloud(this, vpoints, vcolors)
            vpoints = single(vpoints)';
            vcolors = single(vcolors)';
            this.jGLCanvas.draw_cloud(vpoints(:), vcolors(:));
        end
        
        function draw_mesh(this, M)
            V = single(M.vertices)'; 
            F = (int32(M.faces)-1)'; %< 1-indexing
            jM = this.jGLCanvas.draw_mesh(V(:),F(:));
            %--- Normals
            if isfield(M,'vnormals')
                N = single(M.vnormals)';
                jM.set_vnormals(N(:));
            end
            %--- Texture
            if isfield(M,'vtexcoords') && isfield(M,'texture')
                %--- Upload texture coordinates
                VT = single(M.vtexcoords)';
                jM.set_vtexcoords(VT(:));               

                %--- Upload the texture (+swizzle)
                T = single( zeros(numel(M.texture),1) );
                T(1:3:end) = M.texture(:,:,1);
                T(2:3:end) = M.texture(:,:,2);
                T(3:3:end) = M.texture(:,:,3);
                s = size(M.texture);
                jM.set_texture(s(1),s(2),T);                
            end
        end
	end
        
    %--- Static methods        
    methods(Static)
        function compile()
            close all; clear all; clear java; %#ok<CLJAVA,CLALL>
            
            %--- Add the path (location of class files)
            localpath = fileparts(which('GLCanvas'));
            javaaddpath(localpath);
            %--- Trash existing
            delete([localpath filesep '*.class']);
            
            %--- Setup classpath (libraries)
            basepath = fullfile(matlabroot,'java','jarext');
            classpath = pwd();
            classpath = [classpath pathsep fullfile(basepath,'jogl-all.jar')];
            classpath = [classpath pathsep fullfile(basepath,'gluegen-rt.jar')];
            classpath = [classpath pathsep fullfile(basepath,'vecmath.jar')];
            
            [~, javac_version] = system('javac -version');
            javam_version = version('-java');
            jc = javac_version(7:9);
            jm = javam_version(6:8);
            if ~all(jc==jm)
                fprintf('matlab requires: %s\n', javam_version);
                fprintf('system provides: %s\n', javac_version);
                error('java version mismatch');
            end
            
            %--- Compile
            src = [' ' localpath filesep 'JGLCanvas.java'];
            cmd = ['javac -cp "' classpath '"' src];
            failed = system(cmd,'-echo');
            assert(failed==0);
        end
        
        function demo()
            clc, close all; clear all; clear java; %#ok<CLJAVA,CLALL>
            GLCanvas.compile();
            
            %--- Create 1M random points
            npoints = 1000000;
            vpoints = .2*( randn(npoints,3) ) ;
            vcolors = rand(npoints,3);
            fig1 = GLCanvas();
            fig1.draw_cloud(vpoints, vcolors);
            
            %--- Simple planar mesh
            M.vertices = [-1 -1 0; -1 +1 0; +1 +1 0; +1 -1 0];
            M.vtexcoords = [0 0; 0 +1; +1 +1; +1 0];
            M.faces = [1 2 3; 1 3 4];
            t = linspace(0,1,100);
            [XX,YY] = meshgrid(t,t);
            M.texture = cat(3,XX,YY,zeros(size(XX)));
            fig3 = GLCanvas();
            fig3.draw_mesh(M);

            %--- Simple spherical mesh (shaded)
            % load sphere_big.mat M;
            load sphere_small.mat M;
            fig2 = GLCanvas();
            fig2.draw_mesh(M);
            
            %--- Parameterization mesh (checkerboard)
            load camel_lscm.mat M;
            t = linspace(0,2*pi,2048);
            [XX,YY] = meshgrid(t,t);
            C = round(.5*(1+sin(10*XX).*sin(10*YY)));
            M.texture = cat(3,C,C,zeros(size(C)));
            fig4 = GLCanvas();
            fig4.draw_mesh(M);
        end
    end
end