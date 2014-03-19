clc; close all; clear all;
if ~compile(), return; end;

%--- Create some data (1M points)
npoints = 1000000;
vpoints = .2*( randn(npoints,3) ) ;
vcolors = rand(npoints,3);

%--- Create an OpenGL window
view = View3;
cloud = view.scatter(vpoints,vcolors);
cloud.setPointSize(3);

%--- See how slow the Matlab scatter is (only 10k points)
vpoints = vpoints(1:10000,:);
vcolors = vcolors(1:10000,:);
figure, scatter3(vpoints(:,1),vpoints(:,2),vpoints(:,3),1,vcolors)