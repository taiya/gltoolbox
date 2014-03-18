function success = compile()
clc; close all; clear java;
jPath = fullfile(matlabroot,'java','jarext');
cp = [fullfile(jPath,'jogl.jar') pathsep fullfile(jPath,'gluegen-rt.jar') pathsep pwd()];

% Sources to be compiled
srcs = [];

% Entry point
append_src('OpenGLCanvas.java');

% Layered rendering system
append_src('SimpleRenderer.java');
append_src('ArcballRenderer.java');

% Objects that can be rendered + interface
append_src('Object.java');
append_src('Cube.java');
append_src('Mesh.java');
append_src('PointCloud.java');

cmd = ['javac -cp "' cp '"' srcs];
retval = system(cmd,'-echo');
success = (retval==0);
javaaddpath(pwd);

function append_src(file)
    srcs = [srcs, ' ', file];
end

end % compile