classdef View3
    properties
        jCanvas = [];
        hCanvas = [];
    end
    
    methods
        %--- Constructor
        function this = View3()
            this.jCanvas = OpenGLCanvas();
            [~, this.hCanvas] = javacomponent(this.jCanvas);
            set(this.hCanvas, 'Units', 'normalized', 'Position', [0 0 1 1]);
        end
        
        function ret = mesh(this, vertices, faces, normals)
            % data expexted as: [x1 y1 z1  x2 y2 z2 ...] 
            verts = single(vertices)'; 
            faces = (int32(faces)-1)'; %remove 1-indexing
            normals = single(normals)';
            ret = MeshRenderer(verts(:),faces(:),normals(:));
            this.jCanvas.add(ret);
        end
        
        function ret = scatter(this, vpoints, vcolors)
            vpoints = single(vpoints)';
            vcolors = single(vcolors)';
            ret = PointCloud(vpoints(:), vcolors(:));
            this.jCanvas.add(ret);
        end
        
        %--- Example
        function ret = cube(this)
            ret = this.jCanvas.add(CubeRenderer());
        end
    end
        
    %--- Static methods        
    methods(Static)
        compile();
    end
end