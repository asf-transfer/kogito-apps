const merge = require('webpack-merge');
const common = require('./webpack.config.js');
const path = require('path');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common, {
  mode: 'development',
  entry: {
    app: path.resolve(__dirname, './index.tsx')
  },
  output: {
    path: path.resolve('../dist-dev'),
    filename: '[name].js',
    publicPath: '/'
  },
  devtool: 'source-map',
  devServer: {
    contentBase: path.join(__dirname),
    host: HOST,
    port: PORT,
    compress: true,
    inline: true,
    historyApiFallback: true,
    hot: true,
    overlay: true,
    open: true
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader']
      },
      {
        test: /\.s[ac]ss$/i,
        use: ['style-loader', 'css-loader', 'sass-loader']
      }
    ]
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js', '.jsx']
  }
});
