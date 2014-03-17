function success = compile()
clc, clear java;
jPath = fullfile(matlabroot,'java','jarext');
cp = [fullfile(jPath,'jogl.jar') pathsep fullfile(jPath,'gluegen-rt.jar') pathsep pwd()];

srcs = [];
append_src('View3.java');
append_src('TriMesh.java');
append_src('ObjectRenderer.java');
append_src('TrackballRenderer.java');
append_src('ArcBall.java');

cmd = ['javac -cp "' cp '"' srcs];
retval = system(cmd,'-echo');
success = (retval==0);
javaaddpath(pwd);

function append_src(file)
    srcs = [srcs, ' ', file];
end

end % compile