function success = compile()
clc; close all; clear java;
jPath = fullfile(matlabroot,'java','jarext');
cp = [fullfile(jPath,'jogl.jar') pathsep fullfile(jPath,'gluegen-rt.jar') pathsep pwd()];

srcs = [];
append_src('OpenGLCanvas.java');
append_src('Renderer.java');
append_src('TrackballRenderer.java');
append_src('ObjectRenderer.java');
append_src('CubeRenderer.java');
append_src('PointCloud.java');
append_src('MeshRenderer.java');
append_src('ArcBall.java');

cmd = ['javac -cp "' cp '"' srcs];
retval = system(cmd,'-echo');
success = (retval==0);
javaaddpath(pwd);

function append_src(file)
    srcs = [srcs, ' ', file];
end

end % compile